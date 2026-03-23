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
@DisplayName("Testes de Unidade: UserService")
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
    @DisplayName("Cenário: Busca de Usuário")
    class GetUserTests {
        @Test
        @DisplayName("Deve retornar usuário quando ID existe")
        void shouldReturnUser_WhenIdExists() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

            User result = userService.getUserById(1L);

            assertNotNull(result);
            assertEquals("Admin User", result.name());
            verify(userRepository).findById(1L);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando ID não existe")
        void shouldThrowException_WhenIdDoesNotExist() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
        }

        @Test
        @DisplayName("Deve retornar todos usuarios criados")
        void shouldReturnAllUsers_WhenRequesterIsAdmin() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
            when(userRepository.findAll()).thenReturn(List.of(admin, dev));

            List<User> userList = userService.getAllUsers(1L);
            assertEquals(2, userList.size());
            verify(userRepository).findAll();
        }

        @Test
        @DisplayName("Should throw ForbiddenException when Requester is not Admin")
        void shouldThrowException_WhenRequesterIsNotAdmin() {
            when(userRepository.findById(2L)).thenReturn(Optional.of(dev));
            assertThrows(ForbiddenException.class, () -> userService.getAllUsers(2L));
            verify(userRepository, never()).findAll();
        }
    }

    @Nested
    @DisplayName("Cenário: Atualização de Perfil")
    class UpdateProfileTests {
        @Test
        @DisplayName("Deve atualizar perfil com sucesso quando o próprio usuário solicita")
        void shouldUpdateProfile_WhenOwnerRequests() {
            UpdateUserProfileRequest request = new UpdateUserProfileRequest("new@test.com", "Novo Nome");
            when(userRepository.findById(2L)).thenReturn(Optional.of(dev));
            when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

            User result = userService.updateProfile(2L, request, 2L);

            assertEquals("Novo Nome", result.name());
            assertEquals("new@test.com", result.email().value());
        }

        @Test
        @DisplayName("Deve lançar ForbiddenException quando um usuário tenta atualizar o perfil de outro")
        void shouldThrowForbidden_WhenRequesterIsNotOwner() {
            UpdateUserProfileRequest request = new UpdateUserProfileRequest("any@test.com", "Nome");

            assertThrows(ForbiddenException.class, () ->
                    userService.updateProfile(2L, request, 1L));
        }
    }

    @Nested
    @DisplayName("Cenário: Atualização de Senha")
    class PasswordTests {
        @Test
        @DisplayName("Deve atualizar senha com sucesso")
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
        @DisplayName("Deve lançar PasswordMismatchException quando a nova senha e a confirmação são diferentes")
        void shouldThrowException_WhenNewPasswordsDoNotMatch() {
            UpdatePasswordRequest request = new UpdatePasswordRequest(
                    "Current123", "NewPass1", "NewPass2");

            assertThrows(PasswordMismatchException.class, () ->
                    userService.updatePassword(2L, request, 2L));
        }
    }

    @Nested
    @DisplayName("Cenário: Desativação de Usuário")
    class DeactivationTests {
        @Test
        @DisplayName("Admin deve conseguir desativar outro usuário")
        void shouldDeactivateUser_WhenAdminIsRequester() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
            when(userRepository.findById(2L)).thenReturn(Optional.of(dev));
            when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

            User result = userService.deactivateUser(2L, 1L);

            assertFalse(result.isActive());
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Admin não deve conseguir desativar a própria conta")
        void shouldThrowException_WhenAdminTriesToDeactivateSelf() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

            assertThrows(ForbiddenException.class, () ->
                    userService.deactivateUser(1L, 1L));
        }
    }
}