package com.tweetapp.tweet.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.tweetapp.tweet.model.AuthResponse;
import com.tweetapp.tweet.model.Message;
import com.tweetapp.tweet.model.Reply;
import com.tweetapp.tweet.model.Tweet;
import com.tweetapp.tweet.repo.TweetRepository;
import com.tweetapp.tweet.service.TweetServiceImpl;

@ExtendWith(MockitoExtension.class)
public class TweetServiceTest {
	
	@Mock
	private TweetRepository tweetRepo;
	
	@InjectMocks
	private TweetServiceImpl tweetService;
	
	private static List<Tweet> tweets = new ArrayList<>();
	
	private static List<Tweet> spTweets = new ArrayList<>();
	
	
	private static String username = "";
	private static String id = "";
	private static Message msg;
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
	@DisplayName("Get all tweets test - 1")
	public void testGetAllTweet1() {
		//starting test
		when(tweetRepo.findAll(Sort.by("tweetScore").descending().and(Sort.by("createdDate")
				.descending()))).thenReturn(tweets);
		assertEquals(3, this.tweetService.getAllTweets(-1, -1).size());
	}
	
	@Test
	@DisplayName("Get all tweets test - 2")
	public void testGetAllTweet2() {
		// initialize the tweetRepo return
		Page<Tweet> pageOfTweets = new PageImpl<>(tweets);
		//starting test
		when(tweetRepo.findAll(PageRequest.of(0, 3, Sort.by("tweetScore")
				.descending().and(Sort.by("createdDate").descending())))).thenReturn(pageOfTweets);
		assertEquals(3, this.tweetService.getAllTweets(0, 3).size());
	}
	
	@Test
	@DisplayName("Post tweet")
	public void testPostTweet() {
		username = "fl1";
		msg.setMsg("message1");
		authRes.setName("F1 L1");
		authRes.setUsername("fl1");
		Tweet dummy = new Tweet();
		dummy.setName(authRes.getName());
		dummy.setUsername(authRes.getUsername());
		dummy.setMessage(msg.getMsg());
		try {
			tweetService.postTweet(username, msg, authRes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		verify(tweetRepo).save(dummy);
		
	}
	
	@Test
	@DisplayName("Get Tweets By Username")
	public void testGetTweetsByUsername() {
		// init
		username = "fl2";
		List<Tweet> ret = new ArrayList<Tweet>();
		ret.add(tweets.get(2));
		//test
		when(tweetRepo.findByUsername(username)).thenReturn(ret);
		assertEquals(ret, tweetService.getTweetsByUsername(username));
	}
	
	@Test
	@DisplayName("Update Tweet By Id")
	public void testUpdateTweetById() {
		// init
		id = "102";
		username = "fl2";
		msg.setMsg("new message2");
		authRes.setName("F2 L2");
		authRes.setUsername(username);
		Tweet ret = tweets.get(1);
		ret.setName("First Last");
		ret.setCreatedDate(new Date());
		//test
		when(tweetRepo.findById(id)).thenReturn(Optional.of(ret));
		try {
			tweetService.updateTweetById(id, username, msg, authRes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		verify(tweetRepo).findById(id);
		verify(tweetRepo).save(ret);
	}
	
	@Test
	@DisplayName("Delete tweet by id")
	public void testDeleteTweetById() {
		//init
		id = "101";
		username = "fl1";
		authRes.setName("F1 L1");
		authRes.setUsername(username);
		Tweet ret = tweets.get(0);
		// test
		when(tweetRepo.findById(id)).thenReturn(Optional.of(ret));
		try {
			tweetService.deleteTweetById(username, id, authRes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		verify(tweetRepo).deleteById(id);
	}
	
	@Test
	@DisplayName("Like Tweet")
	public void testLikeTweet() {
		//init
		id = "104";
		username = "fl4";
		Tweet ret = spTweets.get(0);
		// test
		when(tweetRepo.findById(id)).thenReturn(Optional.of(ret));
		try {
			tweetService.likeTweet(id, username);;
		} catch (Exception e) {
			e.printStackTrace();
		}
		Set<String> likes = ret.getLikes();
		likes.add(username);
		ret.setLikes(likes);
		ret.setTweetScore(tweetService.calculateTweetScore(ret.getLikes().size(), 0));
		verify(tweetRepo).findById(id);
		verify(tweetRepo).save(ret);
	}
	
	@Test
	@DisplayName("Reply Tweet")
	public void testReplyTweet() {
		//init
		id = "104";
		username = "fl4";
		final String name = "F4 L4";
		msg.setMsg("Reply4");
		Tweet ret = spTweets.get(0);
		// test
		when(tweetRepo.findById(id)).thenReturn(Optional.of(ret));
		try {
			tweetService.replyTweet(name, username, id, msg);;
		} catch (Exception e) {
			e.printStackTrace();
		}
		Stack<Reply> replies = ret.getReplies();
		replies.push(new Reply("F4 L4", "fl4", msg.getMsg(), null));
		ret.setReplies(replies);
		ret.setTweetScore(tweetService.calculateTweetScore(ret.getLikes().size(), ret.getReplies().size()));
		verify(tweetRepo).findById(id);
		verify(tweetRepo).save(ret);
	}
	
	@Test
	@DisplayName("Get tweet by id")
	public void testGetTweetById() {
		//init
		id = "104";
		username = "fl4";
		msg.setMsg("message4");
		Tweet dummy = new Tweet(id, "First Last", username, msg.getMsg(), 0.0f, null, null, new Date());
		//starting test
		when(tweetRepo.findById(id)).thenReturn(Optional.of(dummy));
		assertEquals(id, this.tweetService.getTweetById(id).getId());
	}
	
	@AfterAll
	private static void cleanUp() {
		tweets.clear();
		spTweets.clear();
	}
	
}
