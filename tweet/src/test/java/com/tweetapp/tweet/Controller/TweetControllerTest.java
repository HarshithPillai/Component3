package com.tweetapp.tweet.Controller;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tweetapp.tweet.controller.TweetsController;
import com.tweetapp.tweet.model.AuthResponse;
import com.tweetapp.tweet.model.Message;
import com.tweetapp.tweet.model.Reply;
import com.tweetapp.tweet.model.Tweet;
import com.tweetapp.tweet.service.TweetService;
import com.tweetapp.tweet.service.TweetServiceImpl;

@ExtendWith(MockitoExtension.class)
public class TweetControllerTest {
	
	@Mock
	private TweetService tweetService = new TweetServiceImpl();
	
	@InjectMocks
	private TweetsController tweetController;
	
	private static List<Tweet> tweets = new ArrayList<>();
	private static List<Tweet> spTweets = new ArrayList<>();
	private static String username = "";
	private static String id = "";
	private static Message msg;
	private static ResponseEntity<?> res;
	private static AuthResponse authRes = new AuthResponse();
	
	@BeforeAll
	private static void init() {
		tweets.addAll(Arrays.asList(
				new Tweet("101", "F1 L1", "fl1", "message1", 0.0f, null, null, null),
				new Tweet("102", "F2 L2", "fl2", "message2", 0.0f, null, null, null),
				new Tweet("103", "F3 L3", "fl3", "message3", 0.0f, null, null, null)
				));
		
		Set<String> likes = new HashSet<String>(Arrays.asList("fl1", "fl2"));
		
		Stack<Reply> replies = new Stack<>();
		replies.addAll(Arrays.asList(
				new Reply("F1 L1", "fl1", "reply1"),
				new Reply("F2 L2", "fl2", "reply2")
				));
		
		spTweets.addAll(Arrays.asList(
				new Tweet("104", "F4 L4", "fl4", "message4", 0.0f, likes, null, null),
				
				new Tweet("105", "F5 L5", "fl5", "message5", 0.0f, null, replies, null),
				
				new Tweet("106", "F6 L6", "fl6", "message6", 0.0f, likes, replies, null)
				));
		msg = new Message();
	}
	
	@Test
	@DisplayName("Get all tweets")
	public void testGetAllTweets() {
		//init
		res = new ResponseEntity<List<Tweet>>(tweets, HttpStatus.OK);
		//test
		when(tweetService.getAllTweets(0, 3)).thenReturn(tweets);
		assertEquals(3, tweetController.getAllTweets(0, 3).getBody().size());
	}
	
	@Test
	@DisplayName("Get Tweets by username")
	public void testGetTweetsByUsername() {
		//init
		id = "101";
		username = "fl1";
		//test
		when(tweetService.getTweetsByUsername(username)).thenReturn(new ArrayList<>(Arrays.asList(tweets.get(0))));
		assertEquals(1, tweetController.getTweetsByUsername(username).getBody().size());
	}
	
	@Test
	@DisplayName("Post Tweet")
	public void testAddTweet() {
		//init
		id = "101";
		username = "fl1";
		msg.setMsg("message1");
		authRes.setName("F1 L1");
		authRes.setUsername(username);
		res = new ResponseEntity<String>("Tweet of - " + username + " has been posted!", HttpStatus.OK);
		//test
		try {
			assertEquals(res, tweetController.addTweet(authRes, username, msg));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			verify(tweetService).postTweet(username, msg, authRes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@DisplayName("Update Tweet")
	public void testUpdateTweet() {
		//init
		id = "101";
		username = "fl1";
		msg.setMsg("new message1");
		authRes.setName("F1 L1");
		authRes.setUsername(username);
		res = new ResponseEntity<String>("Tweet of - " + username + " has been updated!", HttpStatus.OK);
		//test
		try {
			assertEquals(res, tweetController.updateTweet(authRes, username, id, msg));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			verify(tweetService).updateTweetById(id, username, msg, authRes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@DisplayName("Delete Tweet")
	public void testDeleteTweet() {
		//init
		id = "101";
		username = "fl1";
		msg.setMsg("new message1");
		authRes.setName("F1 L1");
		authRes.setUsername(username);
		res = new ResponseEntity<String>("Tweet of - " + username + " has been deleted!", HttpStatus.OK);
		//test
		try {
			assertEquals(res, tweetController.deleteTweet(authRes, username, id));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			verify(tweetService).deleteTweetById(username, id, authRes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@DisplayName("Like Tweet")
	public void testLikeTweet() {
		//init
		id = "101";
		username = "fl1";
		res = new ResponseEntity<String>(username + " - has liked a tweet!", HttpStatus.OK);
		//test
		try {
			assertEquals(res, tweetController.likeTweet(username, id));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			verify(tweetService).likeTweet(id, username);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@DisplayName("Reply Tweet")
	public void testReplyTweet() {
		//init
		id = "101";
		username = "fl1";
		msg.setMsg("reply1");
		authRes.setName("F1 L1");
		authRes.setUsername(username);
		res = new ResponseEntity<String>(username + " - has replied to a tweet!", HttpStatus.OK);
		//test
		try {
			assertEquals(res, tweetController.replyTweet(authRes, username, id, msg));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			verify(tweetService).replyTweet(authRes.getName(), username, id, msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@DisplayName("Get tweet by id")
	public void testGetTweetById() {
		//init
		id = "101";
		
		res = new ResponseEntity<>(tweets.get(0), HttpStatus.OK);
		//test
		when(tweetService.getTweetById(id)).thenReturn(tweets.get(0));
		try {
			assertEquals(res, tweetController.getTweetById(id));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterAll
	private static void cleanUp() {
		tweets.clear();
		spTweets.clear();
	}
}
