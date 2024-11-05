package com.capellax.refresh_token_demo.controller;

import com.capellax.refresh_token_demo.dto.AuthRequest;
import com.capellax.refresh_token_demo.dto.JwtResponse;
import com.capellax.refresh_token_demo.dto.RefreshTokenRequest;
import com.capellax.refresh_token_demo.model.RefreshToken;
import com.capellax.refresh_token_demo.model.User;
import com.capellax.refresh_token_demo.service.JwtService;
import com.capellax.refresh_token_demo.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(
            @RequestBody User user
    ) {
        String responseStr =authenticationService.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseStr);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateAndGetToken(
            @RequestBody AuthRequest authRequest
    ) throws UsernameNotFoundException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                )
        );
        if (authentication.isAuthenticated()) {
            RefreshToken refreshToken = authenticationService.createRefreshToken(
                    authRequest.getUsername()
            );
            JwtResponse response = JwtResponse.builder()
                    .accessToken(jwtService.generateToken(authRequest.getUsername()))
                    .refreshToken(refreshToken.getToken())
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<JwtResponse> refreshToken(
            @RequestBody RefreshTokenRequest refreshTokenRequest
    ) {
        JwtResponse jwtResponse = authenticationService
                .findByToken(refreshTokenRequest.getRefreshToken())
                .map(authenticationService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtService.generateToken(user.getName());
                    return JwtResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshTokenRequest.getRefreshToken())
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Refresh token expired or not found."));

        return ResponseEntity.status(HttpStatus.OK).body(jwtResponse);
    }

}
