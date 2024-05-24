package com.cooksys.social_media_1.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserRequestDto {
    
    private CredentialsRequestDto credentials;

    private ProfileRequestDto profile;
}

