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
        @DisplayName("Should return all created users when requester is Admin")
        void shouldReturnAllUsers_WhenRequesterIsAdmin() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
            when(userRepository.findAll()).thenReturn(List.of(admin, dev));

            List<User> userList = userService.getAllUsers(1L);
            assertEquals(2, userList.size());
            verify(userRepository).findAll();
        }

        @Test
        @DisplayName("Should throw ForbiddenException when requester is not Admin")
        void shouldThrowException_WhenRequesterIsNotAdmin() {
            when(userRepository.findById(2L)).thenReturn(Optional.of(dev));
            assertThrows(ForbiddenException.class, () -> userService.getAllUsers(2L));
            verify(userRepository, never()).findAll();
        }
    }

    @Nested
    @DisplayName("Scenario: Profile Update")
    class UpdateProfileTests {
        @Test
        @DisplayName("Should update profile successfully when owner requests")
        void shouldUpdateProfile_WhenOwnerRequests() {
            UpdateUserProfileRequest request = new UpdateUserProfileRequest("new@test.com", "New Name");
            when(userRepository.findById(2L)).thenReturn(Optional.of(dev));
            when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

            User result = userService.updateProfile(2L, request, 2L);

            assertEquals("New Name", result.name());
            assertEquals("new@test.com", result.email().value());
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw ForbiddenException when a user tries to update someone else's profile")
        void shouldThrowForbidden_WhenRequesterIsNotOwner() {
            UpdateUserProfileRequest request = new UpdateUserProfileRequest("any@test.com", "Name");

            assertThrows(ForbiddenException.class, () ->
                    userService.updateProfile(2L, request, 1L));
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Scenario: Update Role")
    class UpdateRoleTest {
        @Test
        @DisplayName("Admin should update another user's role successfully")
        void shouldUpdateRole_WhenAdminIsRequester() {
            User targetUser = new User.Builder()
                    .copy(dev)
                    .withId(3L)
                    .withRole(UserRole.VIEWER)
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
            when(userRepository.findById(3L)).thenReturn(Optional.of(targetUser));
            when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

            User result = userService.updateUserRole(3L, UserRole.DEV, 1L);

            assertNotNull(result);
            assertEquals(3L, result.id());
            assertEquals(UserRole.DEV, result.role());

            verify(userRepository).save(argThat(u ->
                    u.id().equals(3L) && u.role() == UserRole.DEV
            ));
        }

        @Test
        @DisplayName("Admin should not be able to update their own role")
        void shouldThrowException_WhenAdminTriesToUpdateOwnRole() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
            assertThrows(ForbiddenException.class, () ->
                    userService.updateUserRole(1L, UserRole.DEV, 1L));
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Non-admin should not be able to update any user's role")
        void shouldThrowException_WhenNonAdminTriesToUpdateRole() {
            when(userRepository.findById(2L)).thenReturn(Optional.of(dev));
            assertThrows(ForbiddenException.class, () ->
                    userService.updateUserRole(2L, UserRole.DEV, 2L));
            verify(userRepository, never()).save(any(User.class));
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

            userService.updatePassword(2L, request, 2L);

            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw ForbiddenException when user tries to update another user's password")
        void shouldThrowException_WhenUserTriesToUpdateAnotherUsersPassword() {
            UpdatePasswordRequest request = new UpdatePasswordRequest("Current123", "New@123", "New@123");
            assertThrows(ForbiddenException.class, () -> userService.updatePassword(2L, request, 1L));
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw PasswordMismatchException when new password and confirmation do not match")
        void shouldThrowException_WhenNewPasswordsDoNotMatch() {
            UpdatePasswordRequest request = new UpdatePasswordRequest("Current123", "New1", "New2");
            assertThrows(PasswordMismatchException.class, () -> userService.updatePassword(2L, request, 2L));
        }
    }

    @Nested
    @DisplayName("Scenario: User Activation")
    class ActivationTests {
        @Test
        @DisplayName("Admin should activate an inactive user successfully")
        void shouldActivateUser_WhenAdminIsRequester() {
            User inactiveUser = new User.Builder()
                    .copy(dev).withId(3L).withActive(false).build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
            when(userRepository.findById(3L)).thenReturn(Optional.of(inactiveUser));
            when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

            User result = userService.activateUser(3L, 1L);

            assertTrue(result.isActive());
            verify(userRepository).save(argThat(User::isActive));
        }

        @Test
        @DisplayName("Non-admin should not be able to activate any user")
        void shouldThrowException_WhenNonAdminTriesToActivateUser() {
            when(userRepository.findById(2L)).thenReturn(Optional.of(dev));
            assertThrows(ForbiddenException.class, () -> userService.activateUser(2L, 2L));
        }
    }

    @Nested
    @DisplayName("Scenario: User Deactivation")
    class DeactivationTests {
        @Test
        @DisplayName("Admin should be able to deactivate another user")
        void shouldDeactivateUser_WhenAdminIsRequester() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
            when(userRepository.findById(2L)).thenReturn(Optional.of(dev));
            when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

            User result = userService.deactivateUser(2L, 1L);

            assertFalse(result.isActive());
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Non-admin should not be able to deactivate another user's account")
        void shouldThrowException_WhenNonAdminTriesToDeactivateAnotherUser() {
            User targetUser = new User.Builder().copy(dev).withId(3L).withActive(true).build();

            when(userRepository.findById(2L)).thenReturn(Optional.of(dev));
            when(userRepository.findById(3L)).thenReturn(Optional.of(targetUser));

            assertThrows(ForbiddenException.class, () ->
                    userService.deactivateUser(3L, 2L));
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw ForbiddenException when trying to deactivate an already inactive user")
        void shouldThrowException_WhenTriesToDeactivateInactiveUser() {
            User inactiveUser = new User.Builder().copy(dev).withId(3L).withActive(false).build();
            when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
            when(userRepository.findById(3L)).thenReturn(Optional.of(inactiveUser));

            assertThrows(ForbiddenException.class, () -> userService.deactivateUser(3L, 1L));
        }
    }
}