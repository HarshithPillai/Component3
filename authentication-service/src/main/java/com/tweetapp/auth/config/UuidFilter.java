package com.tweetapp.auth.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.filter.OncePerRequestFilter;

@Controller
@Order(Ordered.LOWEST_PRECEDENCE)
public class UuidFilter extends OncePerRequestFilter {
	// Filter for UUID MDC

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// safe uri list, for which UUID is not required

		List<String> safeUri = Arrays.asList("authenticate", "check", "actuator", "swagger", "v3");
		boolean passFlag = false;

		for (String uri : safeUri) {
			if (request.getRequestURI().contains(uri)) {
				passFlag = true;
				break;
			}
		}

		if (passFlag) {
			// feign client call for internal work, no need for uuid
			//System.out.println("Pass");
			filterChain.doFilter(request, response);
		}

		else {

			// Check token and Create UUID
			boolean tokenGenerated = false;
			String uniqueId = "";
			final String uuidTokenFromHeader = request.getHeader("Tweet-App-Token");

			if (uuidTokenFromHeader == null || uuidTokenFromHeader.length() == 0) {
				uniqueId = UUID.randomUUID().toString();
				tokenGenerated = true;
			} else {
				uniqueId = uuidTokenFromHeader;
				tokenGenerated = false;
			}

			// mdc specialID insertion
			MDC.put("specialId", uniqueId);

			// set response header
			if (tokenGenerated) {
				response.setHeader("Tweet-App-Token", uniqueId);
				//System.out.println("UUID Created : " + uniqueId);
			}
			// setting response headers
			response.setHeader("Access-Control-Allow-Headers",
					"x-requested-with, x-auth-token," + " Authorization, content-type, Tweet-App-Token");
			response.setHeader("Access-Control-Expose-Headers", "Tweet-App-Token");
			// Do filter
			filterChain.doFilter(request, response);

			MDC.clear();
		}

	}

}
