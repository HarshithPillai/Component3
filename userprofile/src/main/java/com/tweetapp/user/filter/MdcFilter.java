package com.tweetapp.user.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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

import com.tweetapp.user.model.AuthResponse;

@Controller
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class MdcFilter extends OncePerRequestFilter {
	// Filter for MDC

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// get username from Request-attr
		AuthResponse authRes = (AuthResponse) request.getAttribute("auth_response");
		
		List<String> safeUri = Arrays.asList("authenticate", "check", "actuator", "swagger", "v3");
		boolean passFlag = false;
		
		for(String uri : safeUri) {
			if(request.getRequestURI().contains(uri)) {
				passFlag = true;
				break;
			}
		}
		
		if (Objects.nonNull(authRes)) {
			final String username = authRes.getUsername();

			// mdc username insertion
			MDC.put("username", username);

			// Check token and Create UUID
			boolean tokenGenerated = false;
			String uniqueId = "";
			final String uuidTokenFromHeader = request.getHeader("Tweet-App-Token");

			if (uuidTokenFromHeader == null || uuidTokenFromHeader.length() == 0 || !passFlag) {
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
				// System.out.println("Token Created : " + uniqueId);
			} else {
				// System.out.println("Pass");
			}
			// Do filter
			filterChain.doFilter(request, response);

			MDC.clear();
		} else {
			// Do nothing, just pass
			filterChain.doFilter(request, response);
		}

	}

}
