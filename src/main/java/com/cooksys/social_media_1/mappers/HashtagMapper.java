package com.cooksys.social_media_1.mappers;

import com.cooksys.social_media_1.dtos.HashtagResponseDto;
import com.cooksys.social_media_1.entities.Hashtag;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HashtagMapper {

    List<HashtagResponseDto> entitiesToDtos(List<Hashtag> hashtags);

}
