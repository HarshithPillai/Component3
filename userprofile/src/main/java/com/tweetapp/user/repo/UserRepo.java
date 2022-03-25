package com.tweetapp.user.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.tweetapp.user.model.User;
import com.tweetapp.user.model.UserSearchResultByKeyword;

public interface UserRepo extends MongoRepository<User, String> {

    @Query(value = "{ $or: [ { 'firstName' : /?0/ }, { 'lastName' : /?0/ }, { 'username' : /?0/ } ] }", 
	    fields = "{ 'firstName': 1, 'lastName': 1, 'username': 1, '_id': 1 }")
    List<UserSearchResultByKeyword> findByKeyword(String keyword);
}
