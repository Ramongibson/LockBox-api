package com.ramongibson.lockboxapi.service;

import com.ramongibson.lockboxapi.dto.LoginDTO;
import com.ramongibson.lockboxapi.dto.UserDTO;
import com.ramongibson.lockboxapi.model.User;
import com.ramongibson.lockboxapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser_Success() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newuser");
        userDTO.setPassword("password123!");
        userDTO.setEmail("newuser@example.com");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123!")).thenReturn("encodedPassword");

        String result = authService.registerUser(userDTO);

        assertEquals("User registered successfully", result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegisterUser_UsernameAlreadyExists() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("existinguser");
        userDTO.setPassword("password123!");
        userDTO.setEmail("existinguser@example.com");

        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(new User()));

        String result = authService.registerUser(userDTO);

        assertEquals("Username already exists", result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testRegisterUser_InternalError() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newuser");
        userDTO.setPassword("password123!");
        userDTO.setEmail("newuser@example.com");

        when(userRepository.findByUsername("newuser")).thenThrow(new RuntimeException("Database error"));

        String result = authService.registerUser(userDTO);

        assertEquals("Registration failed due to an internal error", result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testLoginUser_Success() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("existinguser");
        loginDTO.setPassword("password123!");

        User mockUser = new User();
        mockUser.setUsername("existinguser");
        mockUser.setPassword("encodedPassword");

        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password123!", "encodedPassword")).thenReturn(true);

        String result = authService.loginUser(loginDTO);

        assertEquals("Login successful", result);
    }

    @Test
    public void testLoginUser_IncorrectPassword() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("existinguser");
        loginDTO.setPassword("wrongpassword");

        User mockUser = new User();
        mockUser.setUsername("existinguser");
        mockUser.setPassword("encodedPassword");

        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        String result = authService.loginUser(loginDTO);

        assertEquals("Incorrect password", result);
    }

    @Test
    public void testLoginUser_UsernameNotFound() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("unknownuser");
        loginDTO.setPassword("password123!");

        when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

        String result = authService.loginUser(loginDTO);

        assertEquals("Username not found", result);
    }

    @Test
    public void testLoginUser_InternalError() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("existinguser");
        loginDTO.setPassword("password123!");

        when(userRepository.findByUsername("existinguser")).thenThrow(new RuntimeException("Database error"));

        String result = authService.loginUser(loginDTO);

        assertEquals("Login failed due to an internal error", result);
    }
}