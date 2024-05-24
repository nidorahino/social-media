package com.cooksys.social_media_1.services.Impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cooksys.social_media_1.dtos.HashtagResponseDto;
import com.cooksys.social_media_1.dtos.TweetResponseDto;
import com.cooksys.social_media_1.entities.Hashtag;
import com.cooksys.social_media_1.entities.Tweet;
import com.cooksys.social_media_1.exceptions.NotFoundException;
import com.cooksys.social_media_1.mappers.HashtagMapper;
import com.cooksys.social_media_1.mappers.TweetMapper;
import com.cooksys.social_media_1.repositories.HashtagRepository;
import com.cooksys.social_media_1.repositories.TweetRepository;
import com.cooksys.social_media_1.services.HashtagService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

    private final HashtagRepository hashtagRepository;
    private final HashtagMapper hashtagMapper;
    private final TweetMapper tweetMapper;
    private final TweetRepository tweetRepository;
    
	private List<Tweet> checkForActiveTweets(List<Tweet> tweets) {
		List<Tweet> activeTweets = new ArrayList<>();
		for (Tweet t : tweets) {
			if (!t.isDeleted())
				activeTweets.add(t);
		}
		
		return activeTweets;
	}

    public List<HashtagResponseDto> retrieveHashtags()
    {
        return hashtagMapper.entitiesToDtos(this.hashtagRepository.findAll());
    }

    public List<TweetResponseDto> retrieveLabeledHashtag(String label)
    {
        Optional<Hashtag> optionalHashtag = hashtagRepository.findByLabel(label);
        if(optionalHashtag.isEmpty())
        	throw new NotFoundException("Cannot find a hashtag with the given label.");
        
        List<Tweet> tweets = checkForActiveTweets(optionalHashtag.get().getTweets());
		Collections.sort(tweets, Comparator.comparing(Tweet::getPosted).reversed());

        return tweetMapper.entitestoDtos(tweets);
    }
}
