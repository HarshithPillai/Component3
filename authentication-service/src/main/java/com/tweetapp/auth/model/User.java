package com.tweetapp.auth.model;

import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String id;

    private String firstName;

    private String lastName;

    private Date dob;

    @NotBlank
    @Size(max = 20)
    private String username;

    private String gender;

    @NotBlank
    @Size(max = 50)
    @Email
    @Indexed(unique = true)
    private String email;

    @NotBlank
    @Size(max = 120)
    @JsonIgnore
    @Indexed(unique = true)
    private String password;

    @JsonIgnore
    private String securityAnswer1;

    @JsonIgnore
    private String securityAnswer2;

    private boolean logStatus = true;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createdDate = new Date();

    public String getFullName() {
	return this.getFirstName() + " " + this.getLastName();
    }
}