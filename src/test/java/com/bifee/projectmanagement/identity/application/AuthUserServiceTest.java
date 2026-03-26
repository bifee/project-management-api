package com.bifee.projectmanagement.identity.application;

import com.bifee.projectmanagement.identity.application.dto.auth.AuthenticationResponse;
import com.bifee.projectmanagement.identity.application.dto.auth.UserRegistrationRequest;
import com.bifee.projectmanagement.identity.domain.Email;
import com.bifee.projectmanagement.identity.domain.Password;
import com.bifee.projectmanagement.identity.domain.User;
import com.bifee.projectmanagement.identity.domain.UserRepository;
import com.bifee.projectmanagement.identity.domain.UserRole;
import com.bifee.projectmanagement.identity.infrastructure.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests: AuthService")
class AuthUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthUserService authService;

    private final String rawPassword = "Password123";


    @Nested
    @DisplayName("Scenario: User Registration")
    class RegisterTests {

        @Test
        @DisplayName("Should register a new user successfully")
        void shouldRegisterUser_WhenEmailIsUnique() {
            UserRegistrationRequest request = new UserRegistrationRequest("New User", "new@example.com", rawPassword, UserRole.DEV);

            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            String encodedPassword = "encoded_password_hash";
            when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
            when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

            AuthenticationResponse authResponse = authService.register(request);

            assertNotNull(authResponse);
            assertEquals("New User", authResponse.username());
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw Exception when email is already in use")
        void shouldThrowException_WhenEmailAlreadyExists() {
            UserRegistrationRequest request = new UserRegistrationRequest("User", "test@example.com", rawPassword, UserRole.DEV);
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

            assertThrows(RuntimeException.class, () -> authService.register(request));
            verify(userRepository, never()).save(any());
        }
    }
}