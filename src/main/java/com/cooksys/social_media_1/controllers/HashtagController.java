package com.cooksys.social_media_1.controllers;

import com.cooksys.social_media_1.dtos.HashtagResponseDto;
import com.cooksys.social_media_1.dtos.TweetResponseDto;
import com.cooksys.social_media_1.services.HashtagService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class HashtagController {

    private final HashtagService hashtagService;
    @GetMapping
    public List<HashtagResponseDto> retrieveHashtags()
    {
        //Return list of all tags
        return hashtagService.retrieveHashtags();

    }
    @GetMapping("/{label}")
    public List<TweetResponseDto> retrieveLabeledHashtag(@PathVariable("label") String label)
    {
        return hashtagService.retrieveLabeledHashtag(label);
    }
}
