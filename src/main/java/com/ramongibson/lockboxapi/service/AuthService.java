package com.ramongibson.lockboxapi.service;

import com.ramongibson.lockboxapi.dto.LoginDTO;
import com.ramongibson.lockboxapi.dto.UserDTO;
import com.ramongibson.lockboxapi.model.User;
import com.ramongibson.lockboxapi.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String registerUser(UserDTO userDTO) {
        try {
            String username = StringUtils.lowerCase(userDTO.getUsername());
            String email = StringUtils.lowerCase(userDTO.getEmail());

            log.info("Attempting to register user with username: {}", username);

            if (userRepository.findByUsername(username).isPresent()) {
                log.error("Registration failed: Username '{}' already exists", username);
                return "Username already exists";
            }

            User user = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(userDTO.getPassword()))
                    .email(email)
                    .build();

            userRepository.save(user);
            log.info("User '{}' registered successfully", username);
            return "User registered successfully";
        } catch (Exception e) {
            log.error("Error occurred during user registration: {}", e.getMessage(), e);
            return "Registration failed due to an internal error";
        }
    }

    public String loginUser(@Valid LoginDTO userDTO) {
        try {
            String username = StringUtils.lowerCase(userDTO.getUsername());
            log.info("Attempting to log in user with username: {}", username);

            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
                    log.info("Login successful for user: {}", username);
                    return "Login successful";
                } else {
                    log.warn("Login failed: Incorrect password for user: {}", username);
                    return "Incorrect password";
                }
            } else {
                log.warn("Login failed: Username '{}' not found", username);
                return "Username not found";
            }
        } catch (Exception e) {
            log.error("Error occurred during login: {}", e.getMessage(), e);
            return "Login failed due to an internal error";
        }
    }
}