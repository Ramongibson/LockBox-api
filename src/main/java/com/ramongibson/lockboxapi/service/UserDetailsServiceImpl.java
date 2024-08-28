package com.ramongibson.lockboxapi.service;

import com.ramongibson.lockboxapi.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            log.debug("Loading user by username: {}", username);

            com.ramongibson.lockboxapi.model.User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.error("User not found with username: {}", username);
                        return new UsernameNotFoundException("User not found with username: " + username);
                    });

            log.info("User found with username: {}", username);

            return User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles("USER")
                    .build();
        } catch (UsernameNotFoundException e) {
            throw e;  // Re-throw exception to be handled by frontend
        } catch (Exception e) {
            log.error("Error occurred while loading user by username: {}", e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred while loading user details");
        }
    }
}