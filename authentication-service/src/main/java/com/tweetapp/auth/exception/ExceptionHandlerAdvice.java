package com.tweetapp.auth.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerAdvice {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<String> exception(Exception e) {
		LOGGER.error(e.getMessage());
		return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
	}
}
