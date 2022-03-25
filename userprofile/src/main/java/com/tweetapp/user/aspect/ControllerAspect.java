package com.tweetapp.user.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class ControllerAspect {
	
	private Logger logger = null;
	
	@Before("execution(* com.tweetapp.user.controller.UserProfileController.*(..))")
	public void beforeCall(JoinPoint jp) {
		logger = LoggerFactory.getLogger(jp.getTarget().getClass());
		String className = jp.getSignature().getName();
		logger.info("Method being called : " + className);
	}
}
