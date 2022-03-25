package com.tweetapp.tweet.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tweetapp.tweet.model.Tweet;

@Repository
public interface TweetRepository extends MongoRepository<Tweet, String> {

	// find by username
	public List<Tweet> findByUsername(String username);
	
//	// sort by scores
//	public List<Tweet> findAllByScores(Pageable pageable);
	
}
