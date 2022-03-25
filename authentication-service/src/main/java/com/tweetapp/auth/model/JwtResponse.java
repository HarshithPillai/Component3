package com.tweetapp.auth.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwttoken;
    private final Date expiredIn;
    private String username;

    @Getter
    @Setter
    private String fullname;

    public void setUsername(String username) {
	this.username = username;
    }

    public String getUsername() {
	return username;
    }

    public Date getExpiredIn() {
	return expiredIn;
    }

    public JwtResponse(String jwttoken, Date expiredIn, String username, String fullname) {
	this.jwttoken = jwttoken;
	this.expiredIn = expiredIn;
	this.username = username;
	this.fullname = fullname;
    }

    public JwtResponse(String jwttoken, Date expiredIn) {
	this.jwttoken = jwttoken;
	this.expiredIn = expiredIn;
    }

    public String getToken() {
	return this.jwttoken;
    }

}