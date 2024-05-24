package com.cooksys.social_media_1.services;

public interface ValidateService {

    boolean validateUsernameExistence(String username);
    
    boolean validateUsernameAvailable(String username);

	boolean validateLabelExistence(String label);
}
