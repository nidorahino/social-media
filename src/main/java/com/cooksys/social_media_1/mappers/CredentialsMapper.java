package com.cooksys.social_media_1.mappers;

import org.mapstruct.Mapper;

import com.cooksys.social_media_1.dtos.CredentialsRequestDto;
import com.cooksys.social_media_1.entities.Credentials;

@Mapper(componentModel = "spring")
public interface CredentialsMapper {

	Credentials dtoToEntity(CredentialsRequestDto credentialsRequestDto);
}
