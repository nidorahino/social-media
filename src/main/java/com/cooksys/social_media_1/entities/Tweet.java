package com.cooksys.social_media_1.entities;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
public class Tweet {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "author")
	private User author;

	@CreatedDate
	private Timestamp posted = Timestamp.valueOf(LocalDateTime.now());
	
	private boolean deleted = false;
	
	private String content;
	
	@ManyToOne
    @JoinColumn(name = "inReplyTo")
	private Tweet inReplyTo;
	
	@ManyToOne
    @JoinColumn(name = "repostOf")
	private Tweet repostOf;
	
	@OneToMany(mappedBy = "inReplyTo")
    private List<Tweet> replies;
	
	@OneToMany(mappedBy = "repostOf")
    private List<Tweet> reposts;
	
    @ManyToMany(mappedBy = "likedTweets")
    private List<User> likedByUsers;
    
    @ManyToMany(mappedBy = "tweetsMentionedIn")
    private List<User> mentionedUsers;
    
    @ManyToMany
    @JoinTable(
        name = "tweet_hashtags",
        joinColumns = @JoinColumn(name = "tweet_id"),
        inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    private List<Hashtag> hashtags;

}
