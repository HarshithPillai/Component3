package com.tweetapp.tweet.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.tweetapp.tweet.model.AuthResponse;

@FeignClient(name="Authorization-MS", url = "${ms.auth}")
@Component
public interface AuthClient {

	@GetMapping(value="/authenticate")
	public ResponseEntity<AuthResponse> authenticate(@RequestHeader("Authorization") String auth);
}
