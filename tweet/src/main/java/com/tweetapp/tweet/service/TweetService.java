package com.tweetapp.tweet.service;

import java.util.List;

import com.tweetapp.tweet.model.AuthResponse;
import com.tweetapp.tweet.model.Message;
import com.tweetapp.tweet.model.Tweet;

public interface TweetService {

	List<Tweet> getAllTweets(int page, int size);

	List<Tweet> getTweetsByUsername(String username);

	void postTweet(String username, Message tweetmsg, AuthResponse authRes) throws Exception;

	void updateTweetById(String id, String username, Message msg, AuthResponse authRes) throws Exception;

	void deleteTweetById(String username, String id, AuthResponse authRes) throws Exception;

	void likeTweet(String id, String username) throws Exception;

	void replyTweet(String name, String username, String id, Message msg) throws Exception;

	Tweet getTweetById(String id);

}
