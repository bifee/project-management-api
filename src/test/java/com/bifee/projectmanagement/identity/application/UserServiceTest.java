package com.bifee.projectmanagement.identity.application;

import com.bifee.projectmanagement.identity.application.dto.user.UpdatePasswordRequest;
import com.bifee.projectmanagement.identity.application.dto.user.UpdateUserProfileRequest;
import com.bifee.projectmanagement.identity.domain.*;
import com.bifee.projectmanagement.shared.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests: UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User admin;
    private User dev;

    @BeforeEach
    void setUp() {
        admin = new User.Builder()
                .withId(1L)
                .withName("Admin User")
                .withEmail(new Email("admin@test.com"))
                .withPassword(new Password("EncodedPassword123"))
                .withRole(UserRole.ADMIN)
                .withActive(true)
                .build();

        dev = new User.Builder()
                .withId(2L)
                .withName("Developer User")
                .withEmail(new Email("dev@test.com"))
                .withPassword(new Password("EncodedPassword456"))
                .withRole(UserRole.DEV)
                .withActive(true)
                .build();
    }

    @Nested
    @DisplayName("Scenario: User Search")
    class GetUserTests {
        @Test
        @DisplayName("Should return user when ID exists")
        void shouldReturnUser_WhenIdExists() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

            User result = userService.getUserById(1L);

            assertNotNull(result);
            assertEquals("Admin User", result.name());
            verify(userRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when ID does not exist")
        void shouldThrowException_WhenIdDoesNotExist() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
        }

        @Test
        @DisplayName("Should return all created users")
        void shouldReturnAllUsers() {
            when(userRepository.findAll()).thenReturn(List.of(admin, dev));

            List<User> userList = userService.getAllUsers();
            assertEquals(2, userList.size());
            verify(userRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Scenario: Profile Update")
    class UpdateProfileTests {
        @Test
        @DisplayName("Should update profile successfully")
        void shouldUpdateProfile() {
            UpdateUserProfileRequest request = new UpdateUserProfileRequest("new@test.com", "New Name");
            when(userRepository.findById(2L)).thenReturn(Optional.of(dev));
            when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

            User result = userService.updateProfile(2L, request);

            assertEquals("New Name", result.name());
            assertEquals("new@test.com", result.email().value());
            verify(userRepository).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Scenario: Update Role")
    class UpdateRoleTest {
        @Test
        @DisplayName("Should update user's role successfully")
        void shouldUpdateRole() {
            User targetUser = new User.Builder()
                    .copy(dev)
                    .withId(3L)
                    .withRole(UserRole.VIEWER)
                    .build();

            when(userRepository.findById(3L)).thenReturn(Optional.of(targetUser));
            when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

            User result = userService.updateUserRole(3L, UserRole.DEV);

            assertNotNull(result);
            assertEquals(3L, result.id());
            assertEquals(UserRole.DEV, result.role());

            verify(userRepository).save(argThat(u ->
                    u.id().equals(3L) && u.role() == UserRole.DEV
            ));
        }
    }

    @Nested
    @DisplayName("Scenario: Password Update")
    class PasswordTests {
        @Test
        @DisplayName("Should update password successfully with valid data")
        void shouldUpdatePassword_WhenDataIsValid() {
            UpdatePasswordRequest request = new UpdatePasswordRequest(
                    "Current123", "NewPass@123", "NewPass@123");

            when(userRepository.findById(2L)).thenReturn(Optional.of(dev));
            when(passwordEncoder.matches("Current123", dev.password().value())).thenReturn(true);
            when(passwordEncoder.matches("NewPass@123", dev.password().value())).thenReturn(false);
            when(passwordEncoder.encode("NewPass@123")).thenReturn("newEncoded");

            userService.updatePassword(2L, request);

            verify(userRepository).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Scenario: User Activation")
    class ActivationTests {
        @Test
        @DisplayName("Should activate an inactive user successfully")
        void shouldActivateUser() {
            User inactiveUser = new User.Builder()
                    .copy(dev).withId(3L).withActive(false).build();

            when(userRepository.findById(3L)).thenReturn(Optional.of(inactiveUser));
            when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

            User result = userService.activateUser(3L);

            assertTrue(result.isActive());
            verify(userRepository).save(argThat(User::isActive));
        }
    }

    @Nested
    @DisplayName("Scenario: User Deactivation")
    class DeactivationTests {
        @Test
        @DisplayName("Should deactivate user")
        void shouldDeactivateUser() {
            when(userRepository.findById(2L)).thenReturn(Optional.of(dev));
            when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

            User result = userService.deactivateUser(2L);

            assertFalse(result.isActive());
            verify(userRepository).save(any(User.class));
        }
    }
}
