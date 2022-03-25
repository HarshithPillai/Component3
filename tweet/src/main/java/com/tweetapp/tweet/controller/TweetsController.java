 package com.tweetapp.tweet.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.tweet.model.AuthResponse;
import com.tweetapp.tweet.model.Message;
import com.tweetapp.tweet.model.Tweet;
import com.tweetapp.tweet.service.TweetService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/tweets")
public class TweetsController {

	@Autowired
	private TweetService tweetService;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(TweetsController.class);
	
	@GetMapping("/check")
	public ResponseEntity<String> check(){
		LOGGER.info("Checking Tweet-MS...");
		return new ResponseEntity<String>("Tweets-MS is running", HttpStatus.OK);
	}
	
	@GetMapping("/all")
	public ResponseEntity<List<Tweet>> getAllTweets(@RequestParam(required = false, defaultValue = "-1") int page,
			@RequestParam(required = false, defaultValue = "-1") int size){
		// need to create an algo for showing all tweets(trending wise)
		LOGGER.info("All tweets. Page : " + page + ", Size : " + size);
		
		return new ResponseEntity<List<Tweet>>(tweetService.getAllTweets(page, size), HttpStatus.OK);
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<List<Tweet>> getTweetsByUsername(@PathVariable String username){
		// code to check for valid username
		LOGGER.info("Tweets by Id. Username : " + username);
		return new ResponseEntity<List<Tweet>>(tweetService.getTweetsByUsername(username), HttpStatus.OK);
	}

	@PostMapping("/{username}/add")
	public ResponseEntity<String> addTweet(@RequestAttribute(name = "auth_response", required = false) AuthResponse authRes,
			@PathVariable String username,
			@RequestBody Message tweetmsg) throws Exception{
		// code to check for valid username
		LOGGER.info("Posting. Username : " + username);
		tweetService.postTweet(username, tweetmsg, authRes);
		LOGGER.info("Posted. Username : " + username);
		return new ResponseEntity<String>("Tweet of - " + username + " has been posted!", HttpStatus.OK);
		
	}

	@PutMapping("/{username}/update/{id}")
	public ResponseEntity<String> updateTweet(@RequestAttribute(name = "auth_response", required = false) AuthResponse authRes, 
			@PathVariable String username, 
			@PathVariable String id, @RequestBody Message msg) throws Exception{
		
		LOGGER.info("Updating. Tweet_Id : " + id + ", Username : " + username);
		
		tweetService.updateTweetById(id, username, msg, authRes);
		
		LOGGER.info("Updated. Tweet_Id : " + id + ", Username : " + username);
		return new ResponseEntity<String>("Tweet of - " + username + " has been updated!", HttpStatus.OK);			
		
	}

	@DeleteMapping("/{username}/delete/{id}")
	public ResponseEntity<String> deleteTweet(@RequestAttribute(name = "auth_response", required = false) AuthResponse authRes,
			@PathVariable String username,
			@PathVariable String id) throws Exception{
		
		LOGGER.info("Deleting. Tweet_Id : " + id + ", Username : " + username);
		tweetService.deleteTweetById(username, id, authRes);
		
		LOGGER.info("Deleted. Tweet_Id : " + id + ", Username : " + username);
		return new ResponseEntity<String>("Tweet of - " + username + " has been deleted!", HttpStatus.OK);
		
		
	}

	@PutMapping("/{username}/like/{id}")
	public ResponseEntity<String> likeTweet(@PathVariable String username,
			@PathVariable String id) throws Exception{
		LOGGER.info("Liking. Tweet_Id : " + id + ", Username : " + username);
		
		tweetService.likeTweet(id, username);
		
		LOGGER.info("Liked. Tweet_Id : " + id + ", Username : " + username);
		return new ResponseEntity<String>(username + " - has liked a tweet!", HttpStatus.OK);
		
	}
	
	@PostMapping("/{username}/reply/{id}")
	public ResponseEntity<String> replyTweet(@RequestAttribute(name = "auth_response", required = false) AuthResponse authRes, 
			@PathVariable String username, 
			@PathVariable String id, @RequestBody Message msg) throws Exception{
		
		LOGGER.info("Replying. Tweet_Id : " + id + ", Username : " + username);
		tweetService.replyTweet(authRes.getName(), username, id, msg);
		
		LOGGER.info("Replied. Tweet_Id : " + id + ", Username : " + username);
		return new ResponseEntity<String>(username + " - has replied to a tweet!", HttpStatus.OK);
		
	}

	@GetMapping("/byId/{id}")
	public ResponseEntity<?> getTweetById(@PathVariable String id) throws Exception{
		
		LOGGER.info("Tweet by Id. Tweet_Id : " + id);
		return new ResponseEntity<Tweet>(tweetService.getTweetById(id), HttpStatus.OK);
	}
}