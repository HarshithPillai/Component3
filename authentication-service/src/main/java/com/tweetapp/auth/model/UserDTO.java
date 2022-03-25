package com.tweetapp.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String dob;
    private String gender;
    private String ans1;
    private String ans2;

    @Getter
    @Setter
    private String username;

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public String getLastName() {
	return lastName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    public String getDob() {
	return dob;
    }

    public void setDob(String dob) {
	this.dob = dob;
    }

    public String getGender() {
	return gender;
    }

    public void setGender(String gender) {
	this.gender = gender;
    }

    public String getAns1() {
	return ans1;
    }

    public String getAns2() {
	return ans2;
    }

    public void setAns2(String ans2) {
	this.ans2 = ans2;
    }

    public void setAns1(String ans1) {
	this.ans1 = ans1;
    }
}