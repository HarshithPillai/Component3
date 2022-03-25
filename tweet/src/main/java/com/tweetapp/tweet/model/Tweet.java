package com.tweetapp.tweet.model;


import java.util.Date;
import java.util.Set;
import java.util.Stack;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("Tweets")
public class Tweet {
	
	@Id
	private String id;
	private String name; //"First Last";	// default value for rough. it will be set from AuthFeign response in service.
	private String username;
	private String message;
	
	@JsonIgnore
	private float tweetScore = 0.0f;
	
	private Set<String> likes = null;
	private Stack<Reply> replies = null;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private Date createdDate = new Date();
	
}
