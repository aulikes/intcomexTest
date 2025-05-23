package com.intcomex.rest.api.service;

import com.intcomex.rest.api.dto.AuthRequest;
import com.intcomex.rest.api.dto.AuthResponse;
import com.intcomex.rest.api.security.JwtUtil;
import com.intcomex.rest.api.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void authenticate_shouldReturnToken() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setUsername("admin");
        request.setPassword("1234");

        UserDetails mockUser = new User("admin", "1234", Collections.emptyList());
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(mockUser);
        when(jwtUtil.generateToken(mockUser)).thenReturn("mock-token");

        // Act
        AuthResponse response = authService.authenticate(request);

        // Assert
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("admin", "1234"));
        assertEquals("mock-token", response.getToken());
    }
}
