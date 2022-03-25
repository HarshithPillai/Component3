package com.tweetapp.user.model;

import java.util.List;

import lombok.Data;

@Data
public class UserProfile {
    private User user;
    private List<Tweet> tweets;
}
