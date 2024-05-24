package com.cooksys.social_media_1.services.Impl;

import com.cooksys.social_media_1.dtos.*;
import com.cooksys.social_media_1.entities.Credentials;
import com.cooksys.social_media_1.entities.Hashtag;
import com.cooksys.social_media_1.entities.Tweet;
import com.cooksys.social_media_1.entities.User;
import com.cooksys.social_media_1.exceptions.BadRequestException;
import com.cooksys.social_media_1.exceptions.NotAuthorizedException;
import com.cooksys.social_media_1.exceptions.NotFoundException;
import com.cooksys.social_media_1.mappers.CredentialsMapper;
import com.cooksys.social_media_1.mappers.HashtagMapper;
import com.cooksys.social_media_1.mappers.TweetMapper;
import com.cooksys.social_media_1.mappers.UserMapper;
import com.cooksys.social_media_1.repositories.HashtagRepository;
import com.cooksys.social_media_1.repositories.TweetRepository;
import com.cooksys.social_media_1.repositories.UserRepository;
import com.cooksys.social_media_1.services.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

	private final TweetRepository tweetRepository;
	private final TweetMapper tweetMapper;
	private final UserRepository userRepository;
	private final HashtagRepository hashtagRepository;
	private final CredentialsMapper credentialsMapper;
	private final UserMapper userMapper;
	private final HashtagMapper hashtagMapper;

	// Helper method to validate the credentials passed in
	private Optional<User> validateCredentials(CredentialsRequestDto credentialsRequestDto) {
		if (credentialsRequestDto.getUsername() == null || credentialsRequestDto.getPassword() == null) {
			throw new NotAuthorizedException("The given credentials are invalid. Please try again.");
		}
		Credentials credentials = credentialsMapper.dtoToEntity(credentialsRequestDto);
		Optional<User> user = userRepository.findByCredentialsUsernameAndCredentialsPasswordAndDeletedFalse(
				credentials.getUsername(), credentials.getPassword());

		if (user.isEmpty())
			throw new NotAuthorizedException("The given credentials are invalid. Please try again.");

		return user;
	}

	// Helper method to parse Tweet content for @ mentions
	private void processTweetForMentions(Tweet newTweet) {
		String tweetContent = newTweet.getContent();
		Pattern pattern = Pattern.compile("(?<=@)\\S+");
		Matcher matcher = pattern.matcher(tweetContent);
		while (matcher.find()) {
			Optional<User> user = userRepository.findByCredentialsUsernameAndDeletedFalse(matcher.group());
			if (!user.isEmpty()) {
				User mentionedUser = user.get();
				List<Tweet> tweetsMentionedIn = mentionedUser.getTweetsMentionedIn();
				tweetsMentionedIn.add(newTweet);
				mentionedUser.setTweetsMentionedIn(tweetsMentionedIn);
				userRepository.saveAndFlush(mentionedUser);
			}
		}
	}

	// Helper method to parse Tweet content for # hashtags
	private void processTweetForHashtags(Tweet newTweet) {
		List<Hashtag> tweetHashtags = newTweet.getHashtags();
		if (tweetHashtags == null) {
			tweetHashtags = new ArrayList<>();
		}
		String tweetContent = newTweet.getContent();
		Pattern pattern = Pattern.compile("(?<=#)\\S+");
		Matcher matcher = pattern.matcher(tweetContent);
		while (matcher.find()) {
			// Check if hashtag exists and add to list. Else create new Hashtag
			Optional<Hashtag> hashtag = hashtagRepository.findByLabel(matcher.group());
			if (hashtag.isEmpty()) {
				// Create new hashtag here
				Hashtag newHashtag = new Hashtag();
				newHashtag.setLabel(matcher.group());
				tweetHashtags.add(hashtagRepository.saveAndFlush(newHashtag));
			} else {
				// Add hashtag to list
				tweetHashtags.add(hashtag.get());
			}
		}
		newTweet.setHashtags(tweetHashtags);
		tweetRepository.saveAndFlush(newTweet);
	}

	// Helper method to validate the given username
	private Optional<Tweet> validateTweetId(Long id) {
		Optional<Tweet> tweet = tweetRepository.findByIdAndDeletedFalse(id);
		if (tweet.isEmpty()) {
			throw new NotFoundException("A tweet with the given ID was not found. Please try again.");
		}

		return tweet;
	}

	private List<Tweet> checkForActiveTweets(List<Tweet> tweets) {
		List<Tweet> activeTweets = new ArrayList<>();
		for (Tweet t : tweets) {
			if (!t.isDeleted())
				activeTweets.add(t);
		}
		
		return activeTweets;
	}
	
	public TweetResponseDto getTweet(int id) {
		if (!tweetRepository.existsById((long) id) || tweetRepository.findById((long) id).get().isDeleted()) {
			throw new NotFoundException("Tweet not found");
		}
		return tweetMapper.entityToDto(tweetRepository.findById((long) id).get());
	}

	public TweetResponseDto deleteTweet(Long id, CredentialsRequestDto credentialsRequestDto) {
		Tweet tweet = validateTweetId(id).get();
		User user = validateCredentials(credentialsRequestDto).get();
		if (tweet.getAuthor() != user)
			throw new NotAuthorizedException("Not authorized to delete this tweet.");

		tweet.setDeleted(true);
		tweetRepository.saveAndFlush(tweet);
		return tweetMapper.entityToDto(tweet);
	}

	public void postTweetLike(int id, CredentialsRequestDto credentialsRequestDto) {
		if (!tweetRepository.existsById((long) id) || tweetRepository.findById((long) id).get().isDeleted()) {
			throw new NotFoundException("Tweet not found");
		}
		
		User newUser = validateCredentials(credentialsRequestDto).get();
		Tweet likedTweet = tweetRepository.findById((long)id).get();
		
		if (!newUser.getLikedTweets().contains(likedTweet)) {
			newUser.getLikedTweets().add(tweetRepository.findById((long)id).get());
		}
		
		userRepository.saveAndFlush(newUser);

	}

	public ContextDto getTweetContext(Long id) {
		Tweet context = validateTweetId(id).get();
		List<Tweet> before = new ArrayList<>();
		List<Tweet> after = new ArrayList<>(context.getReplies());
		
		Tweet currentTweet = context;
		while (currentTweet.getInReplyTo() != null) {
			before.add(currentTweet.getInReplyTo());
			currentTweet = currentTweet.getInReplyTo();
		}
		
		for (Tweet t : after) {
		    if (t.getReplies() != null) {
		        after.addAll(t.getReplies());
		    }
		}
		
		Collections.sort(before, Comparator.comparing(Tweet::getPosted).reversed());
		Collections.sort(after, Comparator.comparing(Tweet::getPosted).reversed());
		ContextDto contextDto = new ContextDto();
		contextDto.setBefore(tweetMapper.entitestoDtos(checkForActiveTweets(before)));
		contextDto.setAfter(tweetMapper.entitestoDtos(checkForActiveTweets(after)));
		contextDto.setTarget(tweetMapper.entityToDto(context));
		return contextDto;
	}

	@Override
	public List<TweetResponseDto> getTweetReplies(Long id) {
		// Validate provided Tweet ID
		Tweet tweet = validateTweetId(id).get();

		return tweetMapper.entitiesToDTOs(tweet.getReplies());
	}

	@Override
	public List<TweetResponseDto> getTweetReposts(Long id) {
		// Validate provided Tweet ID
		Tweet tweet = validateTweetId(id).get();

		return tweetMapper.entitiesToDTOs(tweet.getReposts());
	}

	@Override
	public List<UserResponseDto> getTweetMentions(Long id) {
		// Validate given tweet ID
		Tweet tweet = validateTweetId(id).get();

		List<User> mentionedUsers = tweet.getMentionedUsers();

		return userMapper.entitiesToDtos(mentionedUsers);
	}

	@Override
	public TweetResponseDto createNewTweet(TweetRequestDto tweetRequestDto) {
		if (tweetRequestDto.getCredentials() == null) {
			throw new BadRequestException("You must provide you credentials (Username and Password) along with your Tweet content");
		}
		// Create Tweet and User entities and set author of tweet
		Tweet newTweet = tweetMapper.dtoToEntity(tweetRequestDto);
		User userCreatingTweet = validateCredentials(tweetRequestDto.getCredentials()).get();
		newTweet.setAuthor(userCreatingTweet);

		// Check for null content
		if (newTweet.getContent() == null) {
			throw new BadRequestException("The Tweet content is required to create a new Tweet. Please provide the Tweet content.");
		}
		// Check Content for @ Mentions and update to newTweet
		processTweetForMentions(tweetRepository.saveAndFlush(newTweet));

		// Check Content for # Hashtags and update to newTweet
		processTweetForHashtags(tweetRepository.saveAndFlush(newTweet));

		return tweetMapper.entityToDto(newTweet);
	}

	@Override
	public TweetResponseDto createNewReplyTweet(TweetRequestDto tweetRequestDto, Long originalTweetId) {
		// Validate and retrieve Tweet from provided ID
		Tweet originalTweet = validateTweetId(originalTweetId).get();

		// Create Tweet and User entities and set author of tweet
		Tweet newTweet = tweetMapper.dtoToEntity(tweetRequestDto);
		User userCreatingTweet = validateCredentials(tweetRequestDto.getCredentials()).get();
		newTweet.setAuthor(userCreatingTweet);

		// Check for null content
		if (newTweet.getContent() == null) {
			throw new BadRequestException("The Tweet content is required to create a new Tweet. Please provide the Tweet content.");
		}

		// Set inReplyTo to the original Tweet
		newTweet.setInReplyTo(originalTweet);

		// Check Content for @ Mentions and update to newTweet
		processTweetForMentions(tweetRepository.saveAndFlush(newTweet));

		// Check Content for # Hashtags and update to newTweet
		processTweetForHashtags(tweetRepository.saveAndFlush(newTweet));

		return tweetMapper.entityToDto(newTweet);
	}

	private List<User> checkForActiveUsers(List<User> users) {
		List<User> activeUsers = new ArrayList<>();
		for (User u : users) {
			if (!u.isDeleted())
				activeUsers.add(u);
		}

		return activeUsers;
	}

	@Override
	public List<TweetResponseDto> getTweets() {
		return tweetMapper.entitiesToDTOs(tweetRepository.findByDeletedFalseOrderByPostedDesc());
	}

	@Override
	public List<HashtagResponseDto> getTagsById(Long id) {
		return hashtagMapper.entitiesToDtos(validateTweetId(id).get().getHashtags());
	}

	@Override
	public List<UserResponseDto> getUsersByLikes(Long id) {
		return userMapper.entitiesToDtos(checkForActiveUsers(validateTweetId(id).get().getLikedByUsers()));
	}

	@Override
	public TweetResponseDto repostTweet(Long id, CredentialsRequestDto credentialsRequestDto) {
		User user = validateCredentials(credentialsRequestDto).get();
		Tweet originalTweet = validateTweetId(id).get();
		Tweet repostTweet = new Tweet();
		repostTweet.setAuthor(user);
		repostTweet.setRepostOf(originalTweet);
		repostTweet = tweetRepository.saveAndFlush(repostTweet);
		originalTweet.getReposts().add(repostTweet);
		tweetRepository.saveAndFlush(originalTweet);
		return tweetMapper.entityToDto(repostTweet);
	}
}
