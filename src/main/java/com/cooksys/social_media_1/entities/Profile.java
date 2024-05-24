package com.cooksys.social_media_1.entities;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class Profile {

    String firstName;
    String lastName;
    @Nonnull
    String email;
    String phone;
}
