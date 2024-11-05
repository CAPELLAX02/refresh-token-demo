package com.capellax.refresh_token_demo.security;

import com.capellax.refresh_token_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(
            String username
    ) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByName(username);
        return user
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("User (" + username + ") not found."));
    }
}
