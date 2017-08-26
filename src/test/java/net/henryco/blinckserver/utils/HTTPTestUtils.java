package net.henryco.blinckserver.utils;

import org.springframework.http.HttpHeaders;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

/**
 * @author Henry on 25/08/17.
 */
public interface HTTPTestUtils {

	String DEFAULT_URI = "http://localhost";

	static URI newURI(String http, String path, int port) {
		try {
			return new URI(http + ":" + port + path);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	static URI newURI(String path, int port) {
		return newURI(DEFAULT_URI, path, port);
	}


	static HttpHeaders jsonTypeHeader() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Content-Type","application/json");
		return httpHeaders;
	}

	static String randomNumberString(long bound) {
		return Long.toString(Math.abs((long) (new Random().nextGaussian() * bound)));
	}

	static String randomNumberString() {
		return randomNumberString(1_000_000_000L);
	}
}