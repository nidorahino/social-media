package com.cooksys.social_media_1.services;


import com.cooksys.social_media_1.dtos.*;

import java.util.List;

public interface TweetService {

    TweetResponseDto getTweet(int id);
    
    TweetResponseDto deleteTweet(Long id, CredentialsRequestDto credentialsRequestDto);
    
    void postTweetLike(int id, CredentialsRequestDto credentialsRequestDto);
    
    ContextDto getTweetContext(Long id);
    
	List<TweetResponseDto> getTweetReplies(Long id);

	List<TweetResponseDto> getTweetReposts(Long id);
	
	TweetResponseDto createNewTweet(TweetRequestDto tweetRequestDto);

	List<UserResponseDto> getTweetMentions(Long id);

	TweetResponseDto createNewReplyTweet(TweetRequestDto tweetRequestDto, Long id);

	List<TweetResponseDto> getTweets();

	List<HashtagResponseDto> getTagsById(Long id);

	List<UserResponseDto> getUsersByLikes(Long id);

	TweetResponseDto repostTweet(Long id, CredentialsRequestDto credentialsRequestDto);
}
