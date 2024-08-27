package com.ramongibson.lockboxapi.service;

import com.ramongibson.lockboxapi.dto.LoginDTO;
import com.ramongibson.lockboxapi.dto.UserDTO;
import com.ramongibson.lockboxapi.model.User;
import com.ramongibson.lockboxapi.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String registerUser(UserDTO userDTO) {
        String username = StringUtils.lowerCase(userDTO.getUsername());
        String email = StringUtils.lowerCase(userDTO.getEmail());

        if (userRepository.findByUsername(username).isPresent()) {
            return "Username already exists";
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .email(email)
                .build();

        userRepository.save(user);
        return "User registered successfully";
    }

    public String loginUser(@Valid LoginDTO userDTO) {
        String username = StringUtils.lowerCase(userDTO.getUsername());

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
                return "Login successful";
            }
        }
        return null;
    }
}