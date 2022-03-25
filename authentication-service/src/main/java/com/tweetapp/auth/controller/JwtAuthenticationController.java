package com.tweetapp.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.auth.config.JwtTokenUtil;
import com.tweetapp.auth.exception.AuthenticationException;
import com.tweetapp.auth.model.AuthResponse;
import com.tweetapp.auth.model.ChangePassword;
import com.tweetapp.auth.model.ForgotpasswordDTO;
import com.tweetapp.auth.model.JwtRequest;
import com.tweetapp.auth.model.JwtResponse;
import com.tweetapp.auth.model.User;
import com.tweetapp.auth.model.UserDTO;
import com.tweetapp.auth.service.JwtUserDetailsService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class JwtAuthenticationController {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationController.class);

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsService userDetailsService;

	@GetMapping(value = "/check")
	public ResponseEntity<String> check() {
		LOGGER.info("Checking Auth-MS");
		return new ResponseEntity<String>("Auth-MS is running", HttpStatus.OK);
	}

	@PostMapping(value = "/login")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
		LOGGER.info("Logging in!! - creating token");
		UserDetails userDetails = null;
		ResponseEntity<?> res = null;
		try {
			authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
			userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
		} catch (Exception e) {
			LOGGER.error("******* No such user exists!!");
			res = new ResponseEntity<String>("No such user exists!!", HttpStatus.FORBIDDEN);
		}
		if (userDetails != null) {
			JwtResponse jwtResponse = jwtTokenUtil.generateToken(userDetails);
			User user = userDetailsService.findByUsername(authenticationRequest.getUsername());

			// setting MDC
			MDC.put("username", user.getUsername());

			jwtResponse.setUsername(authenticationRequest.getUsername());
			jwtResponse.setFullname(user.getFullName());
			res = new ResponseEntity<JwtResponse>(jwtResponse, HttpStatus.OK);
			LOGGER.info("Logged in successfully!");
		}
		return res;
	}

	@PostMapping(value = "/register")
	public ResponseEntity<?> saveUser(@RequestBody UserDTO user) {
		LOGGER.info("Registering new user!! Saving User for email-id : {}", user.getEmail());
		if (userDetailsService.findByUsername(user.getUsername()) == null) {
			User addedUser = userDetailsService.save(user);
			UserDetails userDetails = userDetailsService.loadUserByUsername(addedUser.getUsername());
			// setting MDC
			MDC.put("username", user.getUsername());

			// creating jwt response
			JwtResponse jwtResponse = jwtTokenUtil.generateToken(userDetails);
			jwtResponse.setUsername(addedUser.getUsername());
			jwtResponse.setFullname(addedUser.getFullName());
			LOGGER.info("Successfully registered user.");
			return ResponseEntity.ok(jwtResponse);
		}
		LOGGER.error("Username already exists.");
		return new ResponseEntity<>("Username Exists", HttpStatus.BAD_REQUEST);

	}

	@GetMapping(value = "/authenticate")
	public ResponseEntity<?> authenticate(@RequestHeader("Authorization") String auth) {
		LOGGER.info("Authenticating user!! auth-token : {}", (auth == null ? "ABSENT" : "PRESENT"));
		String jwtToken = auth.substring(7);
		String username = jwtTokenUtil.getUsernameFromToken(jwtToken);

		// get user details
		User user = userDetailsService.findByUsername(username);
		// setting MDC
		MDC.put("username", user.getUsername());

		if (!user.isLogStatus()) {
			LOGGER.error("******* User has not logged in yet!!");
			return new ResponseEntity<>("User has not logged in yet!!", HttpStatus.FORBIDDEN);
		} else {
			AuthResponse authRes = new AuthResponse(user.getFullName(), user.getUsername());
			LOGGER.info("Successfully authenticated user!");
			return ResponseEntity.ok(authRes);
		}

	}

	@PostMapping(value = "/logout")
	public ResponseEntity<String> logoutUser(@RequestHeader("Authorization") String auth, @RequestBody String status) {

		LOGGER.info("Logging out user!! auth-token : '{}' status : '{}'", (auth == null ? "ABSENT" : "PRESENT"),
				status);
		String jwtToken = auth.substring(7);
		String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
		// get user details
		User user = userDetailsService.findByUsername(username);
		// setting MDC
		MDC.put("username", user.getUsername());
		if (!user.isLogStatus()) {
			LOGGER.error("User has not logged in yet!! status : {}", user.isLogStatus());
			return new ResponseEntity<>("User has not logged in yet!!", HttpStatus.FORBIDDEN);
		} else {
//			System.out.println(status);
			if (status.equalsIgnoreCase("logout")) {
				userDetailsService.logoutUser(username);
			}
			LOGGER.error("User successfully logged-out!!");
			return ResponseEntity.ok("User Logged Out");
		}
	}

	@PostMapping(value = "/forgotpassword")
	public ResponseEntity<?> forgotPassword(@RequestBody ForgotpasswordDTO forgotpasswordDTO) {
		LOGGER.info("Resetting password - Forgot Password");
		// get user details
		User user = userDetailsService.findByUsername(forgotpasswordDTO.getUsername());
		// setting MDC
		MDC.put("username", user.getUsername());
		if (!user.isLogStatus()) {
			LOGGER.error("User has not logged in yet!!");
			return new ResponseEntity<>("User has not logged in yet!!", HttpStatus.FORBIDDEN);
		} else {
			Boolean c = userDetailsService.forgotPassword(forgotpasswordDTO);
			if (c) {
				LOGGER.info("Password Reset Successfull - Forgot Password");
				return ResponseEntity.ok("Password Reset Successfull");
			} else {
				LOGGER.error("Invalid Credentials!! - Forgot Password");
				return new ResponseEntity<>("Invalid", HttpStatus.BAD_REQUEST);
			}
		}
	}

	@PostMapping(value = "/changepassword")
	public ResponseEntity<?> changepassword(@RequestBody ChangePassword change) {
		LOGGER.info("Resetting password - Change Password");
		// get user details
		User user = userDetailsService.findByUsername(change.getUsername());
		// setting MDC
		MDC.put("username", user.getUsername());
		if (!user.isLogStatus()) {
			LOGGER.error("******** User has not logged in yet!!");
			return new ResponseEntity<>("User has not logged in yet!!", HttpStatus.FORBIDDEN);
		} else {
			Boolean c = userDetailsService.passkeychange(change);
			if (c) {
				LOGGER.info("Password Reset Successfull - Change Password");
				return ResponseEntity.ok("Password Changed");
			} else {
				LOGGER.error("Invalid Credentials!! - Change Password");
				return new ResponseEntity<>("Invalid", HttpStatus.BAD_REQUEST);
			}
		}
	}

	private void authenticate(String username, String password) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new AuthenticationException("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new AuthenticationException("INVALID_CREDENTIALS", e);
		}
	}
}