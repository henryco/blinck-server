package net.henryco.blinckserver.security.jwt.service;

import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Date;

/**
 * @author Henry on 23/08/17.
 */
@SuppressWarnings("WeakerAccess")
public abstract class TokenAuthenticationService {


	protected abstract String getTokenSecret();

	protected String getDefaultRole() {
		return "ROLE_USER";
	}

	protected String getTokenHeader() {
		return "Authorization";
	}

	protected String getTokenPrefix() {
		return "Bearer";
	}

	protected Long getExpirationTime() {
		return 864_000_000L;
	}



	public final void addAuthentication(HttpServletResponse res, String username) {
		String JWT = Jwts.builder()
				.setSubject(username)
				.setExpiration(new Date(System.currentTimeMillis() + getExpirationTime()))
				.signWith(SignatureAlgorithm.HS512, getTokenSecret())
				.compact();
		res.addHeader(getTokenHeader(), getTokenPrefix() + " " + JWT);
	}



	public final Authentication getAuthentication(HttpServletRequest request) {

		final String token = request.getHeader(getTokenHeader());
		if (token == null) return null;

		final String user;
		try {
			user = Jwts.parser()
					.setSigningKey(getTokenSecret())
					.parseClaimsJws(token.replace(getTokenPrefix(), "")).getBody()
					.getSubject();
		} catch (JwtException e) { return null; }

		return user == null ? null
				: new UsernamePasswordAuthenticationToken(user, null,
				Collections.singletonList(new SimpleGrantedAuthority(getDefaultRole()))
		);
	}

}