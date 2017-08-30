package net.henryco.blinckserver.mvc.service.profile;

import net.henryco.blinckserver.mvc.model.dao.profile.UserBaseProfileDao;
import net.henryco.blinckserver.mvc.model.entity.profile.UserBaseProfile;
import net.henryco.blinckserver.util.dao.BlinckDaoProvider;
import net.henryco.blinckserver.util.dao.BlinckDaoTemplate;
import net.henryco.blinckserver.util.dao.BlinckDaoTemplateProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Henry on 23/08/17.
 */
@Service
public class UserBaseProfileService extends BlinckDaoProvider<UserBaseProfile, Long> {


	@Autowired
	public UserBaseProfileService(UserBaseProfileDao baseProfileDao) {
		super (((baseProfileDao))) ;
	}



}