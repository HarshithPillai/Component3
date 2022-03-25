package com.tweetapp.auth.model;

import lombok.Data;

@Data
public class ChangePassword {

    private String username;
    private String oldpassword;
    private String newpassword;

}
