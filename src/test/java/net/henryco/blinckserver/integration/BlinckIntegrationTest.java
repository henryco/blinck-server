package net.henryco.blinckserver.integration;

import net.henryco.blinckserver.utils.JsonForm;
import net.henryco.blinckserver.utils.HTTPTestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.http.HttpMethod.GET;

/**
 * @author Henry on 27/08/17.
 */

@RunWith(SpringRunner.class)
@PropertySource("classpath:/static/props/base.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BlinckIntegrationTest {

	protected @LocalServerPort int port;
	protected @Autowired Environment environment;
	protected TestRestTemplate restTemplate;


	protected final ResponseEntity<String> fastGetRequest(String endPoint) {
		return restTemplate.exchange(
				new RequestEntity(
						GET, HTTPTestUtils.newURI(endPoint, port)
				), String.class
		);
	}


	protected final ResponseEntity<String> fastPostRequest(String endPoint, JsonForm postForm) {
		return restTemplate.exchange(
				RequestEntity.post(HTTPTestUtils.newURI(endPoint, port))
						.accept(MediaType.APPLICATION_JSON)
						.body(postForm),
				String.class
		);
	}



	@Before
	public final void setUpIntegrationTest() {
		restTemplate = new TestRestTemplate();
	}



	@Test
	public final void connectionTest() {
		assert !fastGetRequest("/").getStatusCode().is5xxServerError();
	}


}