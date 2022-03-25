package com.tweetapp.user.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.user.feign.TweetClient;
import com.tweetapp.user.model.Tweet;
import com.tweetapp.user.model.User;
import com.tweetapp.user.model.UserProfile;
import com.tweetapp.user.model.UserSearchResultByKeyword;
import com.tweetapp.user.repo.UserRepo;

@CrossOrigin(origins = "*")
@RequestMapping("/users")
@RestController
public class UserProfileController {

	private static final String SERVER_STATUS = "UP & RUNNING";

	private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileController.class);
	
	@Autowired
	private UserRepo userRepo;

	@Autowired
	private TweetClient tweetClient;

	@GetMapping("/check")
	public Health healthCheck() {
		return Health.builder().serverStatus(SERVER_STATUS).build();
	}

	@GetMapping("/search")
	public ResponseEntity<?> searchUser(@RequestParam(name = "q", defaultValue = "", required = true) String key) {
		LOGGER.info("Searching for a user : " + key);
		List<UserSearchResultByKeyword> list = userRepo.findByKeyword(key);
		LOGGER.info("Found : " + list.size() + " users based on the key : " + key);
		return ResponseEntity.ok(list);
	}

	@GetMapping("getById/{id}")
	public ResponseEntity<?> getById(@RequestHeader("Authorization") String token, @PathVariable String id) {
		LOGGER.info("Getting Profile info of ID : " + id);
		Optional<User> user = userRepo.findById(id);
		if (user.isPresent()) {
			UserProfile profile = new UserProfile();
			ResponseEntity<List<Tweet>> response = tweetClient.getTweetsByUsername(token,
					user.get().getUsername());
			profile.setUser(user.get());
			if (response.getStatusCode().is2xxSuccessful()) {
				List<Tweet> tweets = response.getBody();
				profile.setTweets(tweets);
			}
			LOGGER.info("Found the user info of ID : " + id);
			return ResponseEntity.ok(profile);
		} else {
			LOGGER.error("Invalid User inputted");
			throw new RuntimeException("User is not valid");
		}
	}

}
