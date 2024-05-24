package com.cooksys.social_media_1.services;

import com.cooksys.social_media_1.dtos.HashtagResponseDto;
import com.cooksys.social_media_1.dtos.TweetResponseDto;

import java.util.List;

public interface HashtagService {
	
    List<HashtagResponseDto> retrieveHashtags();

    List<TweetResponseDto> retrieveLabeledHashtag(String label);
}
