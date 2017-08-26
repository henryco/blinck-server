package net.henryco.blinckserver.security.details;

import net.henryco.blinckserver.mvc.model.dao.security.UserAuthProfileDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author Henry on 22/08/17.
 */
@Service
public class UserDetailsProfileService implements UserDetailsService {


	private final UserAuthProfileDao authProfileDao;

	@Autowired
	public UserDetailsProfileService(UserAuthProfileDao authProfileDao) {
		this.authProfileDao = authProfileDao;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Long id = Long.decode(username);
		if (!authProfileDao.isExists(id))
			throw new UsernameNotFoundException(username+ " does not exist!");
		return new UserDetailsProfile(authProfileDao.getById(id));
	}

}