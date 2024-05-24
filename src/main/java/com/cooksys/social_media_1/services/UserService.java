package com.cooksys.social_media_1.services;

import java.util.List;

import com.cooksys.social_media_1.dtos.CredentialsRequestDto;
import com.cooksys.social_media_1.dtos.TweetResponseDto;
import com.cooksys.social_media_1.dtos.UserRequestDto;
import com.cooksys.social_media_1.dtos.UserResponseDto;

public interface UserService {

	List<TweetResponseDto>  getUserTweets(String username);

	List<TweetResponseDto> getUserFeed(String username);

	List<UserResponseDto> getUserFollowers(String username);

	List<UserResponseDto> getFollowedUsers(String username);

	void followUser(String username, CredentialsRequestDto credentialsRequestDto);

	UserResponseDto updateUsername(String username, UserRequestDto userRequestDto);

	UserResponseDto postUser(UserRequestDto userRequestDto);
	
	List<TweetResponseDto> getMentions(String username);

	UserResponseDto getOneUser(String username);

	List<UserResponseDto> getUsers();

	UserResponseDto deleteUser(String username, CredentialsRequestDto credentialsRequestDto);

	void unfollow(String username, CredentialsRequestDto credentialsRequestDto);
}
