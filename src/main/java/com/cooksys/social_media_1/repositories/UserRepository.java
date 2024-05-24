package com.cooksys.social_media_1.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cooksys.social_media_1.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByCredentialsUsernameAndDeletedFalse(String username);
	
	Optional<User> findByCredentialsUsernameAndCredentialsPasswordAndDeletedFalse(String username, String password);
	
	List<User> findByDeletedFalse();
}
