package net.henryco.blinckserver.mvc.service.relation.core;

import net.henryco.blinckserver.mvc.model.dao.relation.core.FriendshipDao;
import net.henryco.blinckserver.mvc.model.entity.relation.core.Friendship;
import net.henryco.blinckserver.mvc.model.entity.relation.queue.FriendshipNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Henry on 03/09/17.
 */ @Service
public class FriendshipService {

	private final FriendshipDao friendshipDao;

	@Autowired
	public FriendshipService(FriendshipDao friendshipDao) {
		this.friendshipDao = friendshipDao;
	}


	/**
	 * Create and save new friendship relation between users.
	 * @return <b>ID</b> of saved relation id database.
	 */ @Transactional
	public Long saveFriendshipRelation(FriendshipNotification notification) {

		Friendship friendship = new Friendship();
		friendship.setUser1(notification.getInitiatorId());
		friendship.setUser2(notification.getReceiverId());
		friendship.setDate(new Date(System.currentTimeMillis()));
		return friendshipDao.save(friendship).getId();
	}


	@Transactional
	public List<Friendship> getAllUserRelations(Long user) {
		return friendshipDao.getAllByUserIdOrderByDateDesc(user);
	}


	/**
	 * Delete <b>ALL</b> friendship relations with user.
	 */ @Transactional
	public void deleteAllUserRelations(Long user) {
		friendshipDao.deleteAllByUserId(user);
	}


	/**
	 * Delete friendship relation between users.<br>
	 * <b>Arguments order doesn't matter.</b>
	 */ @Transactional
	public void deleteRelationBetweenUsers(Long user1, Long user2) {
		friendshipDao.deleteRelationBetweenUsers(user1, user2);
	}


	@Transactional
	public Friendship getById(Long id) {
		return friendshipDao.getById(id);
	}


	@Transactional
	public boolean isExistsById(Long id) {
	 	return friendshipDao.isExists(id);
	}


	@Transactional
	public void deleteById(Long id) {
	 	friendshipDao.deleteById(id);
	}

}