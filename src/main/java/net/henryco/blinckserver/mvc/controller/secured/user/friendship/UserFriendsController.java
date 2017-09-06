package net.henryco.blinckserver.mvc.controller.secured.user.friendship;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.henryco.blinckserver.mvc.controller.BlinckController;
import net.henryco.blinckserver.mvc.model.entity.relation.core.Friendship;
import net.henryco.blinckserver.mvc.model.entity.relation.queue.FriendshipNotification;
import net.henryco.blinckserver.mvc.service.relation.core.FriendshipService;
import net.henryco.blinckserver.mvc.service.relation.queue.FriendshipNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.function.BiFunction;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Henry on 04/09/17.
 */ @RestController
@RequestMapping("/protected/user/friends")
public class UserFriendsController implements BlinckController {


	private final FriendshipNotificationService notificationService;
	private final FriendshipService friendshipService;


 	@Autowired
	public UserFriendsController(FriendshipService friendshipService,
								 FriendshipNotificationService notificationService) {
		this.notificationService = notificationService;
		this.friendshipService = friendshipService;
	}


	@Data @NoArgsConstructor @AllArgsConstructor
	private static final class DetailedFriendship
			implements Serializable {
		private Long id;
		private Long friend;
	}



	public @RequestMapping(
			value = "/count",
			method = GET
	) Long getFriendsCount(Authentication authentication) {
		return friendshipService.getFriendsCount(getID(authentication.getName()));
	}



	public @RequestMapping(
			value = "/list",
			method = GET,
			produces = JSON
	) Long[] getFriendList(Authentication authentication,
						   @RequestParam("page") int page,
						   @RequestParam("size") int size) {

		final Long id = getID(authentication.getName());
		BiFunction<Long, Long, Long> chooser = (a, b) -> a.equals(id) ? b : a;

		return friendshipService
				.getAllUserRelations(id, page, size)
				.stream().map(f -> chooser.apply(f.getUser1(), f.getUser2()))
		.toArray(Long[]::new);
	}



	public @RequestMapping(
			value = "/detailed/list",
			method = GET,
			produces = JSON
	) DetailedFriendship[] getDetailedFriendList(Authentication authentication,
												 @RequestParam("page") int page,
												 @RequestParam("size") int size) {

 		final Long id = getID(authentication.getName());
		BiFunction<Long, Long, Long> chooser = (a, b) -> a.equals(id) ? b : a;

		return friendshipService
				.getAllUserRelations(id, page, size)
				.stream().map(f -> new DetailedFriendship(f.getId(), chooser.apply(f.getUser1(), f.getUser2())))
		.toArray(DetailedFriendship[]::new);
	}



	/**
	 * <h1>Friendship response JSON:</h1>
	 *	<h2>
	 * 	[&nbsp;
	 * 		{
	 * 			"friendship": 	LONG, &nbsp;
	 * 			"timestamp": 	DATE/LONG, &nbsp;
	 * 			"user_1": 		LONG, &nbsp;
	 * 			"user_2": 		LONG
	 *		}
	 *	&nbsp;]</h2>
	 *	@see Friendship
	 *
	 */
	public @RequestMapping(
			value = "/detailed",
			method = GET,
			produces = JSON
	) Friendship getDetailedFriendship(Authentication authentication,
									   @RequestParam("id") Long relation) {

 		final Long id = getID(authentication.getName());
 		if (friendshipService.isExistsById(relation)) {
			Friendship friendship = friendshipService.getById(relation);
			if (friendship.getUser2().equals(id) || friendship.getUser1().equals(id))
				return friendship.clone();
		}
		throw new AccessDeniedException("Wrong friendship id");
	}



	public @ResponseStatus(OK) @RequestMapping(
			value = "/add",
			method = {GET, POST}
	) void addFriend(Authentication authentication,
					 @RequestParam("user_id") Long target) {

 		final Long id = getID(authentication.getName());

 		if (friendshipService.isExistsBetweenUsers(id, target)) return;
		if (notificationService.isExistsBetweenUsers(id, target)) return;

		notificationService.addNotification(id, target);
	}



	public @ResponseStatus(OK) @RequestMapping(
			value = "/remove",
			method = {GET, POST, DELETE}
	) void removeFriend(Authentication authentication,
						@RequestParam("user_id") Long target) {

 		final Long id = getID(authentication.getName());
		if (!friendshipService.isExistsBetweenUsers(id, target)) return;

		Friendship friendship = friendshipService.getByUsers(id, target);
		if (friendship.getUser1().equals(id) || friendship.getUser2().equals(id))
			friendshipService.deleteRelationBetweenUsers(id, target);
	}



	public @ResponseStatus(OK) @RequestMapping(
			value = "/request/accept",
			method = {GET, POST}
	) void acceptFriendRequest(Authentication authentication,
							   @RequestParam("user_id") Long target) {

 		final Long id = getID(authentication.getName());
		if (!checkNotificationRequest(id, target)) return;

		friendshipService.addFriendshipRelation(target, id);
		notificationService.deleteByUsers(id, target);
	}



	public @ResponseStatus(OK) @RequestMapping(
			value = "/request/decline",
			method = {GET, POST}
	) void declineFriendRequest(Authentication authentication,
								@RequestParam("user_id") Long target) {

 		final Long id = getID(authentication.getName());
 		if (!checkNotificationRequest(id, target)) return;

 		notificationService.deleteByUsers(id, target);
	}



	public @ResponseStatus(OK) @RequestMapping(
			value = "/request/direct/delete",
			method = {GET, POST, DELETE}
	) void deleteFriendRequest(Authentication authentication,
							   @RequestParam("id") Long reqId) {

		final Long id = getID(authentication.getName());
		if (!notificationService.isExists(reqId)) return;
		if (!notificationService.getById(reqId).getInitiatorId().equals(id)) return;

		notificationService.deleteById(reqId);
	}



	/**
	 * <h1>Friendship notification response JSON:</h1>
	 *	<h2>
	 * 	[&nbsp;
	 * 		{
	 * 			"notification": 	LONG, &nbsp;
	 * 			"from": 			LONG, &nbsp;
	 * 			"to": 				LONG, &nbsp;
	 * 			"timestamp": 		DATE/LONG
	 *		}
	 *	&nbsp;]</h2>
	 *	@see FriendshipNotification
	 *
	 */
	public @RequestMapping(
			value = "/request/list/outcome",
			method = GET
	) FriendshipNotification[] getOutcomeRequestList(Authentication authentication,
													 @RequestParam("page") int page,
													 @RequestParam("size") int size) {

		return notificationService.getAllNotificationByInitiator(
				getID(authentication.getName()), page, size
		).toArray(new FriendshipNotification[0]);
	}



	/**
	 * <h1>Friendship notification response JSON:</h1>
	 *	<h2>
	 * 	[&nbsp;
	 * 		{
	 * 			"notification": 	LONG, &nbsp;
	 * 			"from": 			LONG, &nbsp;
	 * 			"to": 				LONG, &nbsp;
	 * 			"timestamp": 		DATE/LONG
	 *		}
	 *	&nbsp;]</h2>
	 *	@see FriendshipNotification
	 *
	 */
	public @RequestMapping(
			value = "/request/list/income",
			method = GET
	) FriendshipNotification[] getIncomeRequestList(Authentication authentication,
												   @RequestParam("page") int page,
												   @RequestParam("size") int size) {

 		return notificationService.getAllNotificationByReceiver(
				getID(authentication.getName()), page, size
		).toArray(new FriendshipNotification[0]);
	}




	private static Long getID(String name) {
		return Long.decode(name);
	}


	private boolean checkNotificationRequest(Long id, Long target) {
		boolean pre = !friendshipService.isExistsBetweenUsers(id, target)
				&& notificationService.isExistsBetweenUsers(id, target);
		return pre && notificationService.getWithUsers(id, target).getReceiverId().equals(id);
	}

}