package net.henryco.blinckserver.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * @author Henry on 22/08/17.
 */
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {


	public JWTLoginFilter(String url, AuthenticationManager authManager) {
		super(new AntPathRequestMatcher(url));
		setAuthenticationManager(authManager);
	}



	@Override
	public Authentication attemptAuthentication(HttpServletRequest req,
												HttpServletResponse res)
			throws AuthenticationException, IOException, ServletException {

		AccountCredentials credentials = new ObjectMapper()
				.readValue(req.getInputStream(), AccountCredentials.class);

		System.out.println(credentials);

		return getAuthenticationManager().authenticate(
				new UsernamePasswordAuthenticationToken(
						credentials.getUsername(),
						credentials.getPassword(),
						Collections.emptyList()
				)
		);
	}



	@Override
	protected void successfulAuthentication(HttpServletRequest req,
											HttpServletResponse res,
											FilterChain chain,
											Authentication auth)
			throws IOException, ServletException {

		TokenAuthService.addAuthentication(res, auth.getName());
		System.out.println("SUCCESSFULLY AUTH");
	}
}