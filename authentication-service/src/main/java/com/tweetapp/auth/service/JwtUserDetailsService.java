package com.tweetapp.auth.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.tweetapp.auth.model.ChangePassword;
import com.tweetapp.auth.model.ForgotpasswordDTO;
import com.tweetapp.auth.model.MyUserDetails;
import com.tweetapp.auth.model.User;
import com.tweetapp.auth.model.UserDTO;
import com.tweetapp.auth.repository.UserRepository;

@Service
public class JwtUserDetailsService implements UserDetailsService {

	@Autowired
	private BCryptPasswordEncoder bcryptEncoder;

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findByUsername(username);

		if (!user.isPresent()) {
			throw new UsernameNotFoundException("Could not find user");
		} else {
			user.get().setLogStatus(true);
			userRepository.save(user.get());
		}

		return new MyUserDetails(user.get());
	}

	public User findByUsername(String username) {
		return userRepository.findByUsername(username).orElse(null);
	}

	public User findByEmail(String email) {
		return userRepository.findByEmail(email).orElse(null);
	}

	// logout
	public void logoutUser(String username) {
		User user = userRepository.findByUsername(username).orElse(null);
		if (user != null) {
			user.setLogStatus(false);
			userRepository.save(user);
		}
	}

	public User save(UserDTO user) {
		User newUser = new User();
		newUser.setEmail(user.getEmail());
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		newUser.setUsername(user.getUsername());
		newUser.setFirstName(user.getFirstName());
		newUser.setLastName(user.getLastName());
		newUser.setGender(user.getGender());
		newUser.setSecurityAnswer1(bcryptEncoder.encode(user.getAns1()));
		newUser.setSecurityAnswer2(bcryptEncoder.encode(user.getAns2()));
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			newUser.setDob(df.parse(user.getDob()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		newUser.setLogStatus(true);
		return userRepository.save(newUser);
	}

	public boolean passkeychange(ChangePassword changePassword) {
		User a = findByUsername(changePassword.getUsername());
//		System.out.println(a.getUsername()+""+a.getPassword());
		if (a == null) {
			return false;
		} else {
			if (bcryptEncoder.matches(changePassword.getOldpassword(), a.getPassword())) {
				a.setPassword(bcryptEncoder.encode(changePassword.getNewpassword()));
//				System.out.println(bcryptEncoder.matches(changePassword.getNewpassword(),a.getPassword()));
				userRepository.save(a);
				return true;
			} else {
				return false;
			}
		}
	}

	public Boolean forgotPassword(ForgotpasswordDTO forgotpasswordDTO) {
		User user = findByUsername(forgotpasswordDTO.getUsername());
		if (user == null) {
			return false;
		}
		if (bcryptEncoder.matches(forgotpasswordDTO.getSecurityAnswer1(), user.getSecurityAnswer1())
				&& bcryptEncoder.matches(forgotpasswordDTO.getSecurityAnswer2(), user.getSecurityAnswer2())) {
			user.setPassword(bcryptEncoder.encode(forgotpasswordDTO.getResetPassword()));
			userRepository.save(user);
			return true;
		} else {
			return false;
		}
	}
}