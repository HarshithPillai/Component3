package com.tweetapp.auth.model;

import java.util.Date;

import lombok.Data;

@Data
public class ForgotpasswordDTO {

    private String username;
    private Date dob;
    private String securityAnswer1;
    private String securityAnswer2;
    private String resetPassword;
}
