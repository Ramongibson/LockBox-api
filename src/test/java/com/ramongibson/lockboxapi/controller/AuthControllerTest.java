package com.ramongibson.lockboxapi.controller;

import com.ramongibson.lockboxapi.dto.LoginDTO;
import com.ramongibson.lockboxapi.dto.UserDTO;
import com.ramongibson.lockboxapi.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser_Success() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newuser");
        userDTO.setPassword("password123!");
        userDTO.setEmail("test@test.com");

        when(authService.registerUser(userDTO)).thenReturn("User registered successfully");

        ResponseEntity<String> response = authController.registerUser(userDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully", response.getBody());
        verify(authService, times(1)).registerUser(userDTO);
    }

    @Test
    public void testRegisterUser_Failure() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("existinguser");
        userDTO.setPassword("password123!");
        userDTO.setEmail("test@test.com");

        when(authService.registerUser(userDTO)).thenReturn("Username already exists");

        ResponseEntity<String> response = authController.registerUser(userDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Username already exists", response.getBody());
        verify(authService, times(1)).registerUser(userDTO);
    }

    @Test
    public void testLoginUser_Success() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("existinguser");
        loginDTO.setPassword("password123!");

        String token = "test-token";
        when(authService.loginUser(loginDTO)).thenReturn(token);

        ResponseEntity<?> response = authController.loginUser(loginDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals(token, responseBody.get("token"));

        verify(authService, times(1)).loginUser(loginDTO);
    }

    @Test
    public void testLoginUser_Failure_InvalidCredentials() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("unknownuser");
        loginDTO.setPassword("wrongpassword");

        when(authService.loginUser(loginDTO)).thenReturn(null);

        ResponseEntity<?> response = authController.loginUser(loginDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
        verify(authService, times(1)).loginUser(loginDTO);
    }
}