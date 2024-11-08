package com.capellax.refresh_token_demo.repository;

import com.capellax.refresh_token_demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByName(String name);

}
