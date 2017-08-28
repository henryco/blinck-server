package net.henryco.blinckserver.unit.auth.token;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import net.henryco.blinckserver.security.jwt.service.TokenAuthenticationService;
import net.henryco.blinckserver.util.test.BlinckTestUtil;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Random;

import static net.henryco.blinckserver.utils.TestUtils.randomNumberString;

/**
 * @author Henry on 27/08/17.
 */
public class PureTokenAuthTest extends TokenAuthTest {

	private static final Long tokenExpTime = 1_000_000L;


	@Test
	public void createdTokenIsNotSimilarTest() throws Exception {

		testLoop.test(() -> {
			final String value = randomNumberString();
			assert !getCoderMethod().invoke(createJwtService(new Random().nextLong()), value).equals(value);
		});
	}


	@Test
	public void tokenSingleServiceCodeDeCodeTest() throws Exception {

		TokenAuthenticationService service = createJwtService(tokenExpTime);

		final Method deCoder = getDeCoderMethod();
		final Method coder = getCoderMethod();

		testLoop.test(() -> {
			final String value = randomNumberString();
			assert deCoder.invoke(service, coder.invoke(service, value)).equals(value);
		});
	}


	@Test
	public void tokenMultiServiceCodeDeCodeSimilarTest() throws Exception {

		testLoop.test(() -> {
			String coded = getCoderMethod().invoke(createJwtService(tokenExpTime), randomNumberString()).toString();

			try {
				getDeCoderMethod().invoke(createJwtService(tokenExpTime), coded);
			} catch (InvocationTargetException e) {
				assert e.getTargetException() instanceof SignatureException;
			}
		});
	}


	@Test
	public void createdTokenMultiServiceIsNotSimilar() throws Exception {

		testLoop.test(() -> {
			final String value = randomNumberString();

			final String tokenOne = getCoderMethod().invoke(createJwtService(tokenExpTime), value).toString();
			final String tokenTwo = getCoderMethod().invoke(createJwtService(tokenExpTime), value).toString();

			assert !tokenOne.equals(tokenTwo);
		});
	}



	@Test
	public void tokenExpirationTimeTest() throws Exception {

		final long expTimeMs = 10L;
		final TokenAuthenticationService service = createJwtService(expTimeMs);
		final Object token = getCoderMethod().invoke(service, randomNumberString());

		for (int i = 0; i < 10; i++) {

			Thread.sleep(expTimeMs + 1);

			try {
				getDeCoderMethod().invoke(service, token);
			} catch (InvocationTargetException e) {
				assert e.getTargetException() instanceof ExpiredJwtException;
			}
		}
	}



	@Test @SuppressWarnings("unchecked")
	public void tokenDefaultRolGrantTest() throws Exception {

		testLoop.test(() -> {

			final String DEFAULT_ROLE = "ROLE_"+randomNumberString();

			Collection<? extends GrantedAuthority> authorities =
					(Collection<? extends GrantedAuthority>)
							getDefaultAuthorityGranterMethod().invoke(getJwtService(DEFAULT_ROLE));

			assert !authorities.isEmpty();
			assert authorities.stream().anyMatch(auth -> auth.getAuthority().equals(DEFAULT_ROLE));
		});
	}


	@Test
	public void tokenGrantAuthorityWithoutDetailsServiceTest() throws Exception {

		testLoop.test(() -> {

			final String ROLE = "ROLE_"+randomNumberString();
			Object aut = getAuthorityGranterMethod().invoke(getJwtService(ROLE), randomNumberString());
			Object def = getDefaultAuthorityGranterMethod().invoke(getJwtService(ROLE));

			assert aut.equals(def);
		});
	}





	private static Method getCoderMethod() {
		return BlinckTestUtil.getMethod(
				TokenAuthenticationService.class,
				"createAuthenticationToken"
		);
	}


	private static Method getDeCoderMethod() {
		return BlinckTestUtil.getMethod(
				TokenAuthenticationService.class,
				"readAuthenticationToken"
		);
	}


	private static Method getDefaultAuthorityGranterMethod() {
		return BlinckTestUtil.getMethod(
				TokenAuthenticationService.class,
				"grantDefaultAuthorities"
		);
	}


	private static Method getAuthorityGranterMethod() {
		return BlinckTestUtil.getMethod(
				TokenAuthenticationService.class,
				"grantAuthorities"
		);
	}


	private static TokenAuthenticationService getJwtService(String DEFAULT_ROLE) {
		return createJwtService(
				tokenExpTime,
				randomNumberString(),
				randomNumberString(),
				DEFAULT_ROLE
		);
	}
}