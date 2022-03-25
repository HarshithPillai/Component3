 package com.tweetapp.tweet.service;


import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.tweetapp.tweet.model.AuthResponse;
import com.tweetapp.tweet.model.Message;
import com.tweetapp.tweet.model.Reply;
import com.tweetapp.tweet.model.Tweet;
import com.tweetapp.tweet.repo.TweetRepository;

@Service
public class TweetServiceImpl implements TweetService{
	
	@Autowired
	private TweetRepository tweetRepository;
	
	private boolean validMessage(Message msg) {
		String message = msg.getMsg().trim();
		if(message.length() > 144 || message.length()==0) {		
			return false;
		}
		if(message.contains("#")) {
			for(String word : message.split("\\s+")) {
				if(word.startsWith("#")) {
					if(word.length()> 51) {		
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public List<Tweet> getAllTweets(int page, int size){
		
		if(page==-1 && size==-1) {		// case-1
			return tweetRepository.findAll(Sort.by("tweetScore").descending().and(Sort.by("createdDate").descending()));
		}
		else {		// case-2
			Pageable sortTweets = PageRequest.of(page, size, Sort.by("tweetScore").descending().and(Sort.by("createdDate").descending()));
			return tweetRepository.findAll(sortTweets).getContent();
		}
	}
	
	public float calculateTweetScore(int countOfLikes, int countOfReplies){
		final float likesWeight = 5.5f;		
		final float repliesWeight = 4.5f;		
	
		return (likesWeight*countOfLikes)+(repliesWeight*countOfReplies);
	}

	public List<Tweet> getTweetsByUsername(String username){
		List<Tweet> tweetList = tweetRepository.findByUsername(username);
		if(tweetList.size()>0) {			
			tweetList.sort(Comparator.comparing(Tweet::getCreatedDate, Comparator.reverseOrder()));
		}
		return tweetList;
	}
	
	public void postTweet(String username, Message message, AuthResponse authResponse) throws Exception {
		if(authResponse.getUsername().equals(username)) {		
			Tweet tweet = null;
			if(this.validMessage(message)) {
				tweet = new Tweet();
				tweet.setName(authResponse.getName());
				tweet.setUsername(username);
				tweet.setMessage(message.getMsg());
				tweetRepository.save(tweet);
			}
			else {
				throw new Exception("Max message size is 144 characters. #-tag can have atmost 50 chars!");
			}
		}
		else {
			throw new Exception("Improper User Details!");
		}
	}


	public void updateTweetById(String id, String username, Message msg, AuthResponse authResponse) throws Exception {
		if(authResponse.getUsername().equals(username)) {			
			Tweet tweet = null;
			tweet = tweetRepository.findById(id).get();		
			if(tweet.getUsername().equals(username)) {	
				if(this.validMessage(msg)) {				
					tweet.setMessage(msg.getMsg());
					tweetRepository.save(tweet);
				}
				else {
					throw new Exception("Max message size is 144characters. #-tag can have atmost 50 chars!");
				}
			}
			else {
				throw new Exception("Username mismatch!");
			}
		}
		else {			
			throw new Exception("Improper User Details!");
		}
	}

	public void deleteTweetById(String username, String id, AuthResponse authResponse) throws Exception {
		if(authResponse.getUsername().equals(username)) {			
			Tweet tweet = null;
			tweet = tweetRepository.findById(id).get();		
			if(tweet.getUsername().equals(username)) {				
				tweetRepository.deleteById(id);				
			}
			else {
				throw new Exception("Username mismatch!");
			}
		}
		else {			
			throw new Exception("Improper User Details!");
		}
	}

	public void likeTweet(String id, String username) throws Exception{
		Tweet tweet = null;
		tweet = tweetRepository.findById(id).get();
		if(tweet.getLikes()==null) {
			Set<String> newLikeList = new HashSet<>();
			newLikeList.add(username);
			tweet.setLikes(newLikeList);
		}
		else {
			Set<String> newLikeList = tweet.getLikes();
			
			//dislike (like - two times)
			if(newLikeList.contains(username)) {
				newLikeList.remove(username);	// remove like
				tweet.setLikes(newLikeList);
			}
			
			// like
			else {				
				newLikeList.add(username);		// add like
				tweet.setLikes(newLikeList);
			}
		}
		// set the score
		int countOfLikes = (tweet.getLikes()==null)? 0 : tweet.getLikes().size();
		int countOfReplies = (tweet.getReplies()==null)? 0 : tweet.getReplies().size();
		
		tweet.setTweetScore(this.calculateTweetScore(countOfLikes, countOfReplies));
		
		tweetRepository.save(tweet);		
	}

	public void replyTweet(String name, String username, String id, Message msg) throws Exception{
		Tweet tweet = null;
		tweet = tweetRepository.findById(id).get();	
		if(!this.validMessage(msg)) {throw new Exception("Max message size can be 144characters. #-tag can have atmost 50characters!!");}
		if(tweet.getReplies()==null) {
			tweet.setReplies(new Stack<Reply>());
			tweet.getReplies().push(new Reply(name, username, msg.getMsg()));
		}
		else {
			tweet.getReplies().push(new Reply(name, username, msg.getMsg()));
		}
		// set the score
		int countOfLikes = (tweet.getLikes()==null)? 0 : tweet.getLikes().size();
		int countOfReplies = (tweet.getReplies()==null)? 0 : tweet.getReplies().size();
		
		tweet.setTweetScore(this.calculateTweetScore(countOfLikes, countOfReplies));
		
		tweetRepository.save(tweet);
	}

	public Tweet getTweetById(String id) {
		Tweet tweet = null;
		tweet = tweetRepository.findById(id).get();
		return tweet;
	}
	
}

