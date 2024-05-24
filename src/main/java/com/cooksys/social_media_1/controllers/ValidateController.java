package com.cooksys.social_media_1.controllers;


import com.cooksys.social_media_1.services.ValidateService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/validate")
@RequiredArgsConstructor
public class ValidateController {
	
	private final ValidateService validateService;

    @GetMapping("/tag/exists/{label}")
    boolean validateLabelExistence(@PathVariable("label") String label)
    {
    	return validateService.validateLabelExistence(label);
    }
    
    @GetMapping("/username/exists/@{username}")
    boolean validateUsernameExistence(@PathVariable("username") String username)
    {
        return validateService.validateUsernameExistence(username);
    }

    @GetMapping("/username/available/@{username}")
    boolean validateUsernameAvailable(@PathVariable("username") String username)
    {
        return validateService.validateUsernameAvailable(username);
    }
}
