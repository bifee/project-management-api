package com.bifee.projectmanagement.identity.application;

import com.bifee.projectmanagement.identity.application.dto.auth.AuthenticationResponse;
import com.bifee.projectmanagement.identity.application.dto.auth.UserLoginRequest;
import com.bifee.projectmanagement.identity.application.dto.auth.UserRegistrationRequest;
import com.bifee.projectmanagement.identity.application.dto.auth.UserLoginRequest;
import com.bifee.projectmanagement.identity.application.dto.auth.UserRegistrationRequest;
import com.bifee.projectmanagement.identity.domain.*;
import com.bifee.projectmanagement.identity.infrastructure.security.TokenService;
import com.bifee.projectmanagement.identity.infrastructure.security.UserDetailsImpl;
import com.bifee.projectmanagement.shared.DuplicateResourceException;
import com.bifee.projectmanagement.shared.ForbiddenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests: AuthService")
class AuthUserServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthUserService authService;

    @Nested
    @DisplayName("Scenario: User Login")
    class LoginTests {
        @Test
        @DisplayName("Should return AuthenticationResponse when credentials are valid")
        void shouldReturnToken_WhenCredentialsAreValid() {
            UserLoginRequest request = new UserLoginRequest("user@test.com", "password123");

            User user = new User.Builder()
                    .withName("Test User")
                    .withEmail(new Email("user@test.com"))
                    .withPassword(new Password("password123"))
                    .withRole(UserRole.DEV)
                    .build();

            UserDetailsImpl userDetails = new UserDetailsImpl(user);

            Authentication auth = mock(Authentication.class);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(auth);

            when(auth.getPrincipal()).thenReturn(userDetails);

            when(tokenService.generateToken(user)).thenReturn("mocked-jwt-token");

            AuthenticationResponse response = authService.login(request);

            assertNotNull(response);
            assertEquals("mocked-jwt-token", response.token());
            assertEquals("Test User", response.username());
            assertEquals("Bearer", response.tokenType());

            verify(authenticationManager).authenticate(any());
            verify(tokenService).generateToken(user);
        }

        @Test
        @DisplayName("Should throw IllegalStateException when principal is of unexpected type")
        void shouldThrowException_WhenPrincipalIsWrongType() {
            UserLoginRequest request = new UserLoginRequest("user@test.com", "pass");
            Authentication auth = mock(Authentication.class);

            when(authenticationManager.authenticate(any())).thenReturn(auth);
            when(auth.getPrincipal()).thenReturn("NotUserDetailsImpl");

            assertThrows(IllegalStateException.class, () -> authService.login(request));
        }

        @Test
        @DisplayName("Should throw BadCredentialsException when password does not match")
        void shouldThrowException_WhenPasswordIsIncorrect() {
            UserLoginRequest request = new UserLoginRequest("user@test.com", "wrong_password");

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Invalid username or password"));

            assertThrows(BadCredentialsException.class, () -> authService.login(request));

            verify(tokenService, never()).generateToken(any());
        }

        @Test
        @DisplayName("Should throw DisabledException when user is inactive")
        void shouldThrowException_WhenUserIsInactive() {
            UserLoginRequest request = new UserLoginRequest("inactive@test.com", "password123");

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new DisabledException("User account is disabled"));

            assertThrows(DisabledException.class, () -> authService.login(request));

            verify(tokenService, never()).generateToken(any());
        }
    }

    @Nested
    @DisplayName("Scenario: User Registration")
    class RegisterTests {

            @Test
            @DisplayName("Should register successfully when email is unique")
            void shouldRegister_WhenEmailIsUnique() {
                // 1. Arrange
                UserRegistrationRequest dto = new UserRegistrationRequest(
                        "John Doe", "john@example.com", "securePass123", UserRole.DEV);

                String encodedPass = "encoded_hash_123";
                // Creating the domain object that represents the saved user
                User savedUser = new User.Builder()
                        .withId(1L)
                        .withName("John Doe")
                        .withEmail(new Email("john@example.com"))
                        .withPassword(new Password(encodedPass))
                        .withRole(UserRole.DEV)
                        .withActive(true)
                        .build();

                // Behavior setup
                when(userRepository.existsByEmail(dto.email())).thenReturn(false);
                when(passwordEncoder.encode(dto.password())).thenReturn(encodedPass);
                // We mock save to return our 'savedUser' instance
                when(userRepository.save(any(User.class))).thenReturn(savedUser);
                when(tokenService.generateToken(savedUser)).thenReturn("valid-jwt-token");

                // 2. Act
                AuthenticationResponse response = authService.register(dto);

                // 3. Assert
                assertNotNull(response);
                assertEquals("valid-jwt-token", response.token());
                assertEquals("John Doe", response.username());
                assertEquals("Bearer", response.tokenType());

                // Verify the exact sequence of calls
                verify(userRepository).existsByEmail(dto.email());
                verify(passwordEncoder).encode(dto.password());
                verify(userRepository).save(any(User.class));
                verify(tokenService).generateToken(savedUser);
            }

            @Test
            @DisplayName("Should throw DuplicateResourceException when email already exists")
            void shouldThrowException_WhenEmailAlreadyExists() {
                // 1. Arrange
                UserRegistrationRequest dto = new UserRegistrationRequest(
                        "John Doe", "exists@example.com", "pass", UserRole.DEV);

                // Simulate that the email is already in use
                when(userRepository.existsByEmail("exists@example.com")).thenReturn(true);

                // 2. Act & Assert
                DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () ->
                        authService.register(dto)
                );

                // 3. Verify that the flow stopped immediately
                verify(passwordEncoder, never()).encode(anyString());
                verify(userRepository, never()).save(any(User.class));
                verify(tokenService, never()).generateToken(any());
            }
        }
}
