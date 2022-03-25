package com.tweetapp.user.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reply {
	
	private String name = "First Last";
	private String username;
	private String replyMessage;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private Date createdDate = new Date();
	
	public Reply(String username, String replyMessage) {
		this.username = username;
		this.replyMessage = replyMessage;
	}
}
