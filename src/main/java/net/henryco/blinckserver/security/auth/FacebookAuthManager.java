package net.henryco.blinckserver.security.auth;

import net.henryco.blinckserver.mvc.service.data.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @author Henry on 23/08/17.
 */
@Component @PropertySource("classpath:/static/props/base.properties")
public class FacebookAuthManager implements AuthenticationManager {


	private @Value("facebook.app.id") String app_id;
	private @Value("facebook.app.namespace") String app_namespace;

	private final UserDetailsService detailsService;
	private final UserDataService userDataService;


	@Autowired
	public FacebookAuthManager(@Qualifier("profileDetailsServiceUser")
										   UserDetailsService detailsService,
							   UserDataService userDataService) {
		this.detailsService = detailsService;
		this.userDataService = userDataService;
	}


	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {

		Object facebook_uid = authentication.getPrincipal();
		Object facebook_token = authentication.getCredentials();

		Facebook facebook = new FacebookTemplate(facebook_token.toString(), app_namespace, app_id);
		if (!facebook.isAuthorized())
			throw new SessionAuthenticationException("FACEBOOK UNAUTHORIZED");

		User userProfile = facebook.userOperations().getUserProfile(facebook_uid.toString());

		if (!userProfile.getId().equals(facebook_uid.toString()))
			throw new BadCredentialsException("Invalid user id or token");

		UserDetails userDetails = loadDetails(userProfile);
		if (!primaryCheck(userDetails))
			throw new InsufficientAuthenticationException("Account is disabled");

		return new UsernamePasswordAuthenticationToken(
				userDetails.getUsername(), null,
				Collections.emptyList()
		);
	}


	private UserDetails loadDetails(User userProfile) {

		try {
			return detailsService.loadUserByUsername(userProfile.getId());
		} catch (UsernameNotFoundException e) {
			userDataService.addNewFacebookUser(userProfile);
			return detailsService.loadUserByUsername(userProfile.getId());
		}
	}

	private boolean primaryCheck(UserDetails userDetails) {

		return userDetails.isEnabled()
				&& userDetails.isAccountNonExpired()
				&& userDetails.isAccountNonLocked()
		&& userDetails.isCredentialsNonExpired();
	}

}