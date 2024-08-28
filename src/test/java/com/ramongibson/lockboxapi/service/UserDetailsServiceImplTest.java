package com.ramongibson.lockboxapi.service;

import com.ramongibson.lockboxapi.model.User;
import com.ramongibson.lockboxapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoadUserByUsername_UserFound() {
        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setPassword("password123!");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password123!", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));

        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("unknownuser");
        });

        verify(userRepository, times(1)).findByUsername("unknownuser");
    }

    @Test
    public void testLoadUserByUsername_ErrorOccurs() {
        when(userRepository.findByUsername("erroruser")).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userDetailsService.loadUserByUsername("erroruser");
        });

        assertEquals("An unexpected error occurred while loading user details", exception.getMessage());

        verify(userRepository, times(1)).findByUsername("erroruser");
    }
}