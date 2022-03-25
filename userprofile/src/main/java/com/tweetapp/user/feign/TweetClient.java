package com.tweetapp.user.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.tweetapp.user.model.Tweet;

@FeignClient(name = "Tweet-MS", url = "${TWEET_URI}")
@Component
public interface TweetClient {
    
    @GetMapping("/check")
    public ResponseEntity<String> check();

    @GetMapping("/tweets/{username}")
    public ResponseEntity<List<Tweet>> getTweetsByUsername(@RequestHeader("Authorization") String token, @PathVariable String username);

}
