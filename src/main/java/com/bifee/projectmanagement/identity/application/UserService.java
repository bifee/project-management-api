package com.bifee.projectmanagement.identity.application;

import com.bifee.projectmanagement.identity.application.dto.UpdatePasswordRequest;
import com.bifee.projectmanagement.identity.application.dto.UpdateUserProfileRequest;
import com.bifee.projectmanagement.identity.application.dto.UserLoginRequest;
import com.bifee.projectmanagement.identity.application.dto.UserRegistrationRequest;
import com.bifee.projectmanagement.identity.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User getUserById(Long userId){
        return userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
    }

    @Transactional
    public List<User> getAllUsers(Long requesterId){
        User requester = getUserById(requesterId);
        if(!requester.role().equals(UserRole.ADMIN)){
            throw new IllegalArgumentException("Only admin can get all users");
        }
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<User> getActiveUsers(){
        return userRepository.findByIsActiveTrue();
    }

    @Transactional
    public List<User> getUsersByRole(UserRole role){
        return userRepository.findByRole(role);

    }

    @Transactional
    public List<User> getUsersByName(String name){
        return userRepository.findByName(name);
    }
    @Transactional
    public User updateProfile(Long userId, UpdateUserProfileRequest request, Long requesterId){
        if(!requesterId.equals(userId)){
            throw new IllegalArgumentException("Only user can update his profile");
        }
        User user = getUserById(userId);
        User.Builder builder = user.mutate();
        if (request.name() != null) {
            builder.withName(request.name());
        }
        if (request.email() != null && !request.email().equals(user.email().value())) {
            if(userRepository.existsByEmail(request.email())){
                throw new IllegalArgumentException("Email already exists");
            }
            builder.withEmail(new Email(request.email()));
        }
        return userRepository.save(builder.build());
    }

    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequest request, Long requesterId){
        if(!userId.equals(requesterId)){
            throw new IllegalArgumentException("Only user can update his password");
        }

        if(!request.newPasswordsMatch()){
            throw new IllegalArgumentException("Passwords do not match");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        if(!passwordEncoder.matches(request.currentPassword(), user.password().value())){
            throw new IllegalArgumentException("Current password does not match");
        }

        if(passwordEncoder.matches(request.newPassword(), user.password().value())){
           throw new IllegalArgumentException("New password cannot be the same as the current password");
        }

        var newEncodedPassword = passwordEncoder.encode(request.newPassword());
        User updateUser = user.mutate().withPassword(new Password(newEncodedPassword)).build();
        userRepository.save(updateUser);
    }

    @Transactional
    public User updateUserRole(Long userId, UserRole newRole, Long adminId){
        User user = getUserById(adminId);
        if(user.role() != UserRole.ADMIN){
            throw new IllegalArgumentException("Only admin can change roles");
        }
        if(adminId.equals(userId)){
            throw new IllegalArgumentException("Admin cannot be update own role");
        }
        User updatedUser = user.mutate().withRole(newRole).build();
        return userRepository.save(updatedUser);
    }

    @Transactional
    public User activateUser(Long userId, Long adminId) {
        User admin = getUserById(adminId);
        if (!admin.isAdmin()) {
            throw new IllegalArgumentException("Only admins can activate users");
        }
        User user = getUserById(userId);
        if (user.isActive()) {
            throw new IllegalArgumentException("User is already active");
        }
        User activatedUser = user.mutate()
                .withActive(true)
                .build();
        return userRepository.save(activatedUser);
    }

    @Transactional
    public User deactivateUser(Long userId, Long requesterId) {
        User requester = getUserById(requesterId);
        User user = getUserById(userId);

        // Validar permissão: Admin ou próprio usuário
        boolean isAdmin = requester.isAdmin();
        boolean isOwner = userId.equals(requesterId);

        if (!isAdmin && !isOwner) {
            throw new IllegalArgumentException("Only admins or the user can deactivate the account");
        }

        // Admin não pode desativar própria conta
        if (isAdmin && isOwner) {
            throw new IllegalArgumentException("Admins cannot deactivate their own account");
        }

        // Já está inativo
        if (!user.isActive()) {
            throw new IllegalArgumentException("User is already inactive");
        }

        User deactivatedUser = user.mutate()
                .withActive(false)
                .build();

        return userRepository.save(deactivatedUser);
    }

    @Transactional
    public void deleteUser(Long userId, Long adminId) {
        User admin = getUserById(adminId);

        if (!admin.isAdmin()) {
            throw new IllegalArgumentException("Only admins can permanently delete users");
        }

        if (userId.equals(adminId)) {
            throw new IllegalArgumentException("Admins cannot delete themselves");
        }

        userRepository.deleteById(userId);
    }

}
