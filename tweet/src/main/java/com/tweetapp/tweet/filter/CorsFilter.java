package com.tweetapp.tweet.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter extends OncePerRequestFilter {
	// filter for handling CORS

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException {

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Allow-Headers",
				"x-requested-with, x-auth-token, content-type, Tweet-App-Token");
		response.setHeader("Access-Control-Expose-Headers", "Tweet-App-Token");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Credentials", "true");

		if (!(request.getMethod().equalsIgnoreCase("OPTIONS"))) { // not pre-flight
			filterChain.doFilter(request, response);
		} else { // pre-flight
			response.setHeader("Access-Control-Expose-Headers", "Tweet-App-Token");
			response.setHeader("Access-Control-Allowed-Methods", "POST, PUT, GET, DELETE");
			response.setHeader("Access-Control-Allow-Headers",
					"x-requested-with, x-auth-token," + " Authorization, content-type, Tweet-App-Token");
			response.setStatus(HttpServletResponse.SC_OK);
		}
	}
}