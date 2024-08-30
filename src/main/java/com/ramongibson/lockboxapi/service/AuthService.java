package com.ramongibson.lockboxapi.service;

import com.ramongibson.lockboxapi.dto.LoginDTO;
import com.ramongibson.lockboxapi.dto.UserDTO;
import com.ramongibson.lockboxapi.model.User;
import com.ramongibson.lockboxapi.repository.UserRepository;
import com.ramongibson.lockboxapi.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public String registerUser(UserDTO userDTO) {
        log.debug("Attempting to register new user: {}", userDTO.getUsername());

        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            log.warn("Registration failed: Username '{}' already exists", userDTO.getUsername());
            return "Username already exists";
        }

        User user = User.builder()
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .email(userDTO.getEmail())
                .build();

        userRepository.save(user);
        log.info("User registered successfully: {}", user.getUsername());
        return "User registered successfully";
    }

    public String loginUser(LoginDTO loginDTO) {
        log.debug("Attempting to login user: {}", loginDTO.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            log.info("User logged in successfully: {}", loginDTO.getUsername());
            return jwt;
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", loginDTO.getUsername(), e);
            return null;
        }
    }
}