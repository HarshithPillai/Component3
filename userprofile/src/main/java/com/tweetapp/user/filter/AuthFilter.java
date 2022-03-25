package com.tweetapp.user.filter;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tweetapp.user.feign.AuthClient;
import com.tweetapp.user.model.AuthResponse;

@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE + 1)
public class AuthFilter extends OncePerRequestFilter {
	// filter for Auth Client

	@Autowired
	private AuthClient authClient;

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		

		final String authorizationHeader = request.getHeader("Authorization");
		final String reqUrl = request.getRequestURI(); // for swagger check

		if (Objects.nonNull(authorizationHeader)) {
			try {
				final ResponseEntity<AuthResponse> authenticateResponse = authClient.authenticate(authorizationHeader);

				if (authenticateResponse.getStatusCode().equals(HttpStatus.OK)) {
					if (authenticateResponse.hasBody()) {
						final AuthResponse authResponse = authenticateResponse.getBody();
						request.setAttribute("auth_response", authResponse);
					}
					filterChain.doFilter(request, response);
				} else {
					throw new RuntimeException("Authentication failed with status : "
							+ authenticateResponse.getStatusCode().getReasonPhrase());
				}
			} catch (Exception e) {
				LOGGER.error("AuthFilter : " + e.getMessage());
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Authentication Failed");
			}
		} else if (reqUrl.contains("swagger") || reqUrl.contains("v3") || reqUrl.contains("actuator")) {
			filterChain.doFilter(request, response);
		} else {
			LOGGER.error("AuthFilter :Authentication Header not found. " + "|| Request URL : " + reqUrl);
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Authentication Header not found");
		}
	}
}
