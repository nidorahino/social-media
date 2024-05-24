package com.cooksys.social_media_1.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cooksys.social_media_1.dtos.CredentialsRequestDto;
import com.cooksys.social_media_1.dtos.TweetResponseDto;
import com.cooksys.social_media_1.dtos.UserRequestDto;
import com.cooksys.social_media_1.dtos.UserResponseDto;
import com.cooksys.social_media_1.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

	private final UserService userService;
	
	@GetMapping("/@{username}/feed")
	public List<TweetResponseDto> getUserFeed(@PathVariable String username) {
		return userService.getUserFeed(username);
	}
	
	@GetMapping("/@{username}/followers")
	public List<UserResponseDto> getUserFollowers(@PathVariable String username) {
		return userService.getUserFollowers(username);
	}
	
	@GetMapping("/@{username}/following")
	public List<UserResponseDto> getFollowedUsers(@PathVariable String username) {
		return userService.getFollowedUsers(username);
	}
	
	@PostMapping("/@{username}/follow")
	public void followUser(@RequestBody CredentialsRequestDto credentialsRequestDto, @PathVariable String username) {
		userService.followUser(username, credentialsRequestDto);
		return;
	}
	
	@PatchMapping("/@{username}")
	public UserResponseDto updateUsername(@RequestBody UserRequestDto userRequestDto, @PathVariable String username) {
		return userService.updateUsername(username, userRequestDto);
	}

	@GetMapping("/@{username}/tweets")
	public List<TweetResponseDto> getUserTweets(@PathVariable("username") String username)
	{
		return userService.getUserTweets(username);
	}
	@PostMapping
	public UserResponseDto postUser(@RequestBody UserRequestDto userRequestDto)
	{
		return userService.postUser(userRequestDto);
	}

	
	@GetMapping
	public List<UserResponseDto> getUsers() {
		return userService.getUsers();
	}
	
	@GetMapping("/@{username}")
	public UserResponseDto getOneUser(@PathVariable String username) {
		return userService.getOneUser(username);
	}
	
	@GetMapping("/@{username}/mentions")
	public List<TweetResponseDto> getMentions(@PathVariable String username) {
		return userService.getMentions(username);
	}
	
	@DeleteMapping("/@{username}")
	public UserResponseDto deleteUser(@PathVariable String username, @RequestBody CredentialsRequestDto credentialsRequestDto) {
		return userService.deleteUser(username, credentialsRequestDto);
	}
	
	@PostMapping("/@{username}/unfollow")
	public void unfollow(@PathVariable String username, @RequestBody CredentialsRequestDto credentialsRequestDto) {
		userService.unfollow(username, credentialsRequestDto);
	}
}
