package com.cooksys.social_media_1.mappers;

import java.util.List;
import org.mapstruct.Mapper;


import com.cooksys.social_media_1.dtos.TweetRequestDto;
import com.cooksys.social_media_1.dtos.TweetResponseDto;
import com.cooksys.social_media_1.entities.Tweet;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface TweetMapper {
    List<TweetResponseDto> entitestoDtos(List<Tweet> tweets);

    TweetResponseDto entityToDto(Tweet tweet);

	List<TweetResponseDto> entitiesToDTOs(List<Tweet> tweets);
	
	Tweet dtoToEntity(TweetRequestDto tweetRequestDto);


}
