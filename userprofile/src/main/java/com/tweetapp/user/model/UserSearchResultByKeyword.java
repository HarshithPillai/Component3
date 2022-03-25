package com.tweetapp.user.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserSearchResultByKeyword {
    private String firstName;
    private String lastName;
    private String username;
    private String _id;
}
