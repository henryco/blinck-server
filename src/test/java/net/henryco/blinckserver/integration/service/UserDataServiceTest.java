package net.henryco.blinckserver.integration.service;

import net.henryco.blinckserver.integration.BlinckIntegrationTest;
import net.henryco.blinckserver.mvc.model.entity.profile.UserBaseProfile;
import net.henryco.blinckserver.mvc.service.data.UserDataService;
import net.henryco.blinckserver.mvc.service.profile.UserAuthProfileService;
import net.henryco.blinckserver.mvc.service.profile.UserBaseProfileService;
import net.henryco.blinckserver.mvc.service.profile.UserNameProfileService;
import net.henryco.blinckserver.util.test.BlinckTestUtil;
import net.henryco.blinckserver.utils.MockFacebookUser;
import net.henryco.blinckserver.utils.TestUtils;
import net.henryco.blinckserver.utils.TestedLoop;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.facebook.api.User;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author Henry on 29/08/17.
 */
public class UserDataServiceTest extends BlinckIntegrationTest {


	private @Autowired UserDataService userDataService;
	private @Autowired UserBaseProfileService baseProfileService;
	private @Autowired UserAuthProfileService authProfileService;
	private @Autowired UserNameProfileService nameProfileService;


	@Test
	public void addAndDeleteUserTest() throws Exception {

		new TestedLoop(100).test(() -> {
			User user = MockFacebookUser.getInstance().createRandom().getUser();

			userDataService.addNewFacebookUser(user);

			assert baseProfileService.isExists(Long.decode(user.getId()));
			userDataService.deleteUser(user.getId());

			assert !baseProfileService.isExists(Long.decode(user.getId()));
			assert !authProfileService.isExists(Long.decode(user.getId()));
			assert !nameProfileService.isExists(Long.decode(user.getId()));
		});
	}


	@Test @Transactional
	public void addEntityTest() throws Exception {

		final User user = MockFacebookUser.getInstance().createRandom().getUser();
		final String[] authorities = {UserDataService.ROLE_USER};

		userDataService.addNewFacebookUser(user);

		Method createUserEntity = BlinckTestUtil.getMethod(
				UserDataService.class, "createUserEntity"
		);
		UserBaseProfile baseProfile = (UserBaseProfile) createUserEntity.invoke(
				null, user, authorities
		);

		assert baseProfileService.getById(baseProfile.getId()).equals(baseProfile);
	}


	@Test
	public void userEntityCreationTest() throws Exception {

		final User user = MockFacebookUser.getInstance().createRandom().getUser();
		final String[] authorities = {
				TestUtils.randomNumberString(),
				TestUtils.randomNumberString()
		};

		Method createUserEntity = BlinckTestUtil.getMethod(
				UserDataService.class, "createUserEntity"
		);

		UserBaseProfile baseProfile = (UserBaseProfile) createUserEntity.invoke(
				null, user, authorities
		);

		assert baseProfile != null;
		assert baseProfile.getUserName() != null;
		assert baseProfile.getAuthProfile() != null;
		assert baseProfile.getId() == baseProfile.getUserName().getId();
		assert baseProfile.getId() == baseProfile.getAuthProfile().getId();
		assert baseProfile.getAuthProfile().isEnabled();
		assert !baseProfile.getAuthProfile().isExpired();
		assert !baseProfile.getAuthProfile().isLocked();
		assert baseProfile.getAuthProfile().getPassword() == null;
		assert Arrays.equals(baseProfile.getAuthProfile().getAuthorityArray(), authorities);
	}


}