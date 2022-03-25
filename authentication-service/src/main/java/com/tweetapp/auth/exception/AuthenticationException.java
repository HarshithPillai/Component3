package com.tweetapp.auth.exception;

public class AuthenticationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AuthenticationException() {
	super();
    }

    public AuthenticationException(String message, Exception e) {
	super(message, e);
    }

}
