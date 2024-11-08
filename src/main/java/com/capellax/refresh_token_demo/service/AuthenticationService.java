package com.capellax.refresh_token_demo.service;

import com.capellax.refresh_token_demo.model.RefreshToken;
import com.capellax.refresh_token_demo.model.User;
import com.capellax.refresh_token_demo.repository.RefreshTokenRepository;
import com.capellax.refresh_token_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String addUser(
            User user
    ) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User added to the system!";
    }

    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Check if a refresh token already exists for the user
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserId(user.getId());

        RefreshToken refreshToken;
        if (existingToken.isPresent()) {
            // Update the existing token's values
            refreshToken = existingToken.get();
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(Instant.now().plusMillis(600000));
        } else {
            // Create a new token if none exists
            refreshToken = RefreshToken.builder()
                    .user(user)
                    .token(UUID.randomUUID().toString())
                    .expiryDate(Instant.now().plusMillis(600000))
                    .build();
        }

        return refreshTokenRepository.save(refreshToken);
    }


    public Optional<RefreshToken> findByToken(
            String token
    ) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(
            RefreshToken token
    ) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " - Refresh token was expired. Please make a new login request");
        }
        return token;
    }

}
