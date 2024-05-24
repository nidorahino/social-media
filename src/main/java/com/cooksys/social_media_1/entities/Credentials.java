package com.cooksys.social_media_1.entities;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class Credentials {
    @Nonnull
    @Column(unique = true)
    private String username;
    @Nonnull
    private String password;
}
