package com.cooksys.social_media_1.controllers;



import com.cooksys.social_media_1.dtos.ContextDto;
import com.cooksys.social_media_1.dtos.CredentialsRequestDto;
import com.cooksys.social_media_1.dtos.TweetResponseDto;
import com.cooksys.social_media_1.services.TweetService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import com.cooksys.social_media_1.dtos.TweetRequestDto;
import com.cooksys.social_media_1.dtos.HashtagResponseDto;

import com.cooksys.social_media_1.dtos.UserResponseDto;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/tweets")
public class TweetController {

    private final TweetService tweetService;
    @GetMapping("/{id}")
   TweetResponseDto getTweet(@PathVariable("id") int id)
    {
        return tweetService.getTweet(id);
    }
    @DeleteMapping("/{id}")
    TweetResponseDto deleteTweet(@PathVariable("id") Long id, @RequestBody CredentialsRequestDto credentialsRequestDto)
    {
        return tweetService.deleteTweet(id,credentialsRequestDto);
    }
    @PostMapping("/{id}/like")
    void postTweetLike(@PathVariable("id") int id, @RequestBody CredentialsRequestDto credentialsRequestDto)
    {
        tweetService.postTweetLike(id, credentialsRequestDto);
    }
    @GetMapping("/{id}/context")
    ContextDto getTweetContext(@PathVariable("id") Long id)
    {
        return tweetService.getTweetContext(id);
    }
	
	@GetMapping("/{id}/replies")
	public List<TweetResponseDto> getTweetReplies(@PathVariable Long id) {
		return tweetService.getTweetReplies(id);
	}
	
	@GetMapping("/{id}/reposts")
	public List<TweetResponseDto> getTweetReposts(@PathVariable Long id) {
		return tweetService.getTweetReposts(id);
	}

	@GetMapping("/{id}/mentions")
	public List<UserResponseDto> getTweetMentions(@PathVariable Long id) {
		return tweetService.getTweetMentions(id);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TweetResponseDto createNewTweet(@RequestBody TweetRequestDto tweetRequestDto) {
		return tweetService.createNewTweet(tweetRequestDto);
	}
	
	@PostMapping("/{id}/reply")
	@ResponseStatus(HttpStatus.CREATED)
	public TweetResponseDto createNewReplyTweet(@RequestBody TweetRequestDto tweetRequestDto, @PathVariable Long id) {
		return tweetService.createNewReplyTweet(tweetRequestDto, id);
	}
	
	@GetMapping
	public List<TweetResponseDto> getTweets() {
		return tweetService.getTweets();
	}
	
	@GetMapping("/{id}/tags")
	public List<HashtagResponseDto> getTagsById(@PathVariable Long id) {
		return tweetService.getTagsById(id);
	}
	
	@GetMapping("/{id}/likes")
	public List<UserResponseDto> getUsersByLikes(@PathVariable Long id) {
		return tweetService.getUsersByLikes(id);
	}
	
	@PostMapping("/{id}/repost")
	public TweetResponseDto repostTweet(@PathVariable Long id, @RequestBody CredentialsRequestDto credentialsRequestDto) {
		return tweetService.repostTweet(id, credentialsRequestDto);

	}

}
