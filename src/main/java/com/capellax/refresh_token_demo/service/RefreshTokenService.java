package com.capellax.refresh_token_demo.service;

import com.capellax.refresh_token_demo.model.RefreshToken;
import com.capellax.refresh_token_demo.repository.RefreshTokenRepository;
import com.capellax.refresh_token_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshToken createRefreshToken(
            String username
    ) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(userRepository.findByName(username).get())
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(600000))
                .build();

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
