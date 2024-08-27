package com.ramongibson.lockboxapi.service;

import com.ramongibson.lockboxapi.dto.LoginDTO;
import com.ramongibson.lockboxapi.dto.UserDTO;
import com.ramongibson.lockboxapi.model.User;
import com.ramongibson.lockboxapi.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String registerUser(UserDTO userDTO) {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            return "Username already exists";
        }

        User user = User.builder()
                .username(userDTO.getUsername().toLowerCase())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .email(userDTO.getEmail().toLowerCase())
                .build();

        userRepository.save(user);
        return "User registered successfully";
    }

    public String loginUser(@Valid LoginDTO userDTO) {
        Optional<User> userOpt = userRepository.findByUsername(userDTO.getUsername().toLowerCase());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
                return "Login successful";
            }
        }
        return null;
    }
}