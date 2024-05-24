package com.cooksys.social_media_1.services.Impl;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cooksys.social_media_1.dtos.CredentialsRequestDto;
import com.cooksys.social_media_1.dtos.TweetResponseDto;
import com.cooksys.social_media_1.dtos.UserRequestDto;
import com.cooksys.social_media_1.dtos.UserResponseDto;
import com.cooksys.social_media_1.entities.Credentials;
import com.cooksys.social_media_1.entities.Profile;
import com.cooksys.social_media_1.entities.Tweet;
import com.cooksys.social_media_1.entities.User;
import com.cooksys.social_media_1.exceptions.BadRequestException;
import com.cooksys.social_media_1.exceptions.NotAuthorizedException;
import com.cooksys.social_media_1.exceptions.NotFoundException;
import com.cooksys.social_media_1.mappers.CredentialsMapper;
import com.cooksys.social_media_1.mappers.TweetMapper;
import com.cooksys.social_media_1.mappers.UserMapper;
import com.cooksys.social_media_1.repositories.UserRepository;
import com.cooksys.social_media_1.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final TweetMapper tweetMapper;
	private final UserMapper userMapper;
	private final CredentialsMapper credentialsMapper;
	

	// Helper method to validate the credentials passed in
	private Optional<User> validateCredentials(CredentialsRequestDto credentialsRequestDto) {
		if (credentialsRequestDto.getUsername() == null || credentialsRequestDto.getPassword() == null) {
			throw new NotAuthorizedException("The given credentials are invalid. Please try again.");
		}
		Credentials credentials = credentialsMapper.dtoToEntity(credentialsRequestDto);
		Optional<User> user = userRepository.findByCredentialsUsernameAndCredentialsPasswordAndDeletedFalse(credentials.getUsername(), credentials.getPassword());
		
		if(user.isEmpty())
			throw new NotAuthorizedException("The given credentials are invalid. Please try again.");
		
		return user;
	}

	// Helper method to validate and return the User for the given username
	private Optional<User> getUser(String username) {
		Optional<User> user = userRepository.findByCredentialsUsernameAndDeletedFalse(username);
		
		if(user.isEmpty()) {
			throw new NotFoundException("The given username was not found. Please try again.");
		}
		
		return user;
	}
	

	@Override
	public List<TweetResponseDto> getUserFeed(String username) {
		// Validate and get user entity
		User user = getUser(username).get();
		
		// Get users followed by this user
		List<User> userFollowing = user.getFollowing();
		
		// Get list of Tweets from this user and add all Tweets from followed users
		List<Tweet> userTweets = user.getTweets();
		for(User followedUser : userFollowing) {
			userTweets.addAll(followedUser.getTweets());
		}
		
		return tweetMapper.entitiesToDTOs(userTweets);
	}

	@Override
	public List<UserResponseDto> getUserFollowers(String username) {		
		// Validate and get user's followers
		List<User> followers = getUser(username).get().getFollowers();
		
		return userMapper.entitiesToDtos(followers);
	}

	@Override
	public List<UserResponseDto> getFollowedUsers(String username) {
		// Validate and get followed users
		List<User> followedUsers = getUser(username).get().getFollowing();
		
		
		return userMapper.entitiesToDtos(followedUsers);
	}

	@Override
	public void followUser(String username, CredentialsRequestDto credentialsRequestDto) {
		// Validate and get user to follow
		User userToFollow = getUser(username).get();
		User providedUser = validateCredentials(credentialsRequestDto).get();
		
		// Check if confirmed given credentials user already follows "username"
		if (providedUser.getFollowing().contains(userToFollow)) {
			throw new BadRequestException("You already follow this user!");
		}
		
		// If all above pass, update following and return void
		providedUser.getFollowing().add(userToFollow);
		userToFollow.getFollowers().add(providedUser);
		userRepository.saveAndFlush(providedUser);
		userRepository.saveAndFlush(userToFollow);
	}

	@Override
	public UserResponseDto updateUsername(String username, UserRequestDto userRequestDto) {
		if (userRequestDto.getCredentials() == null || userRequestDto.getProfile() == null) {
			throw new BadRequestException("Please provide valid credentials AND a valid profile in your request");
		}
		
		// Validate given username
		User userToUpdate = getUser(username).get();
		User newUserData = validateCredentials(userRequestDto.getCredentials()).get();
		
		// Check for credential match
		if (!userToUpdate.getCredentials().equals(newUserData.getCredentials())) {
			throw new NotAuthorizedException("The provided credentials are invalid. Please try again.");
		}
		
		// Update and return username
		Profile newProfile = newUserData.getProfile();
		if (newProfile.getEmail() == null || newProfile.getFirstName() == null || newProfile.getLastName() == null || newProfile.getPhone() == null) {
			return userMapper.entityToDto(userRepository.saveAndFlush(userToUpdate));
		} else {
			userToUpdate.setProfile(newUserData.getProfile());
			return userMapper.entityToDto(userRepository.saveAndFlush(userToUpdate));
		}
	}
	
	private List<Tweet> checkForActiveTweets(List<Tweet> tweets) {
		List<Tweet> activeTweets = new ArrayList<>();
		for (Tweet t : tweets) {
			if (!t.isDeleted())
				activeTweets.add(t);
		}
		
		return activeTweets;
	}


	public List<TweetResponseDto>  getUserTweets(String username)
	{
		List<User> users = this.userRepository.findAll();
		users= users.stream().filter(user-> user.getCredentials().getUsername().equals(username)).collect(Collectors.toList());
		if(users.isEmpty())
			throw new NotFoundException("User not found");

		return tweetMapper.entitestoDtos(users.get(0).getTweets().stream().filter(tweet->{return !tweet.isDeleted();}).collect(Collectors.toList()));

	}

	public UserResponseDto postUser(UserRequestDto userRequestDto)
	{
		if(userRequestDto.getCredentials()==null||userRequestDto.getProfile()==null||userRequestDto.getCredentials().getUsername()==null||userRequestDto.getCredentials().getUsername().isEmpty()||userRequestDto.getCredentials().getPassword()==null||userRequestDto.getCredentials().getPassword().isEmpty()||userRequestDto.getProfile().getEmail()==null||userRequestDto.getProfile().getEmail().isEmpty())
		{
			throw new NotFoundException("Required field are not provided");
		}

		if(userRepository.findAll().stream().anyMatch(user->{return user.getCredentials().getUsername().equals(userRequestDto.getCredentials().getUsername());}) && userRepository.findAll().stream().anyMatch(user->{return user.getCredentials().getPassword().equals(userRequestDto.getCredentials().getPassword());}))
		{
			User olduser = userRepository.findAll().stream().filter(user->{return user.getCredentials().getUsername().equals(userRequestDto.getCredentials().getUsername());}).collect(Collectors.toList()).get(0);
			if(!olduser.isDeleted())
				throw new NotFoundException("User already exist");
			olduser.setDeleted(false);
			userRepository.save(olduser);
			return userMapper.entityToDto(olduser);
		}

		if(userRepository.findAll().stream().anyMatch(user->{return user.getCredentials().getUsername().equals(userRequestDto.getCredentials().getUsername());}) && !userRepository.findAll().stream().anyMatch(user->{return user.getCredentials().getPassword().equals(userRequestDto.getCredentials().getPassword());}))
		{
			throw new NotFoundException("User already exist");
		}

//		if(userRequestDto.getCredentials()==null||userRequestDto.getProfile()==null||userRequestDto.getCredentials().getUsername()==null||userRequestDto.getCredentials().getUsername().isEmpty()||userRequestDto.getCredentials().getPassword()==null||userRequestDto.getCredentials().getPassword().isEmpty()||userRequestDto.getProfile().getEmail()==null||userRequestDto.getProfile().getEmail().isEmpty())
//		{
//			throw new NotFoundException("Required field are not provided");
//		}


		User newuser = userMapper.dtoToEntity(userRequestDto);
		userRepository.save(newuser);
		return userMapper.entityToDto(newuser);

	}

	@Override
	public List<TweetResponseDto> getMentions(String username) {
		List<Tweet> mentionedTweets = checkForActiveTweets(getUser(username).get().getTweetsMentionedIn());
		Collections.sort(mentionedTweets, Comparator.comparing(Tweet::getPosted).reversed());
		return tweetMapper.entitiesToDTOs(mentionedTweets);    
	}

	@Override
	public UserResponseDto getOneUser(String username) {
		return userMapper.entityToDto(getUser(username).get());
	}

	@Override
	public List<UserResponseDto> getUsers() {
		return userMapper.entitiesToDtos(userRepository.findByDeletedFalse());
	}

	@Override
	public UserResponseDto deleteUser(String username, CredentialsRequestDto credentialsRequestDto) {
		User user = validateCredentials(credentialsRequestDto).get();
		User userToDelete = getUser(username).get();
		
		if (user.getId() != userToDelete.getId())
			throw new NotAuthorizedException("Not authorized to delete the given user.");
		
		user.setDeleted(true);
		userRepository.saveAndFlush(user);
		return userMapper.entityToDto(user);
	}

	@Override
	public void unfollow(String username, CredentialsRequestDto credentialsRequestDto) {
		User unfollowingUser = validateCredentials(credentialsRequestDto).get();
		User userToUnfollow = getUser(username).get();
		
		if (!unfollowingUser.getFollowing().contains(userToUnfollow))
			throw new NotFoundException("This following relationship does not exist.");
		
		unfollowingUser.getFollowing().remove(userToUnfollow);
		userToUnfollow.getFollowers().remove(unfollowingUser);
		userRepository.saveAndFlush(unfollowingUser);
		userRepository.saveAndFlush(userToUnfollow);
	}
}
