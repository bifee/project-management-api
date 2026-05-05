package com.bifee.projectmanagement.identity.application;

import com.bifee.projectmanagement.identity.application.dto.user.UpdatePasswordRequest;
import com.bifee.projectmanagement.identity.application.dto.user.UpdateUserProfileRequest;
import com.bifee.projectmanagement.identity.domain.*;
import com.bifee.projectmanagement.shared.DuplicateResourceException;
import com.bifee.projectmanagement.shared.ForbiddenException;
import com.bifee.projectmanagement.shared.PasswordMismatchException;
import com.bifee.projectmanagement.shared.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getActiveUsers(){
        return userRepository.findByIsActiveTrue();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getUsersByRole(UserRole role){
        return userRepository.findByRole(role);

    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getUsersByName(String name){
        return userRepository.findByName(name);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.user.id")
    public User updateProfile(Long userId, UpdateUserProfileRequest request){
        User user = getUserById(userId);
        User.Builder builder = user.mutate();
        if (request.name() != null) {
            builder.withName(request.name());
        }
        if (request.email() != null && !request.email().equals(user.email().value())) {
            if(userRepository.existsByEmail(request.email())){
                throw new DuplicateResourceException("User", "email", request.email());
            }
            builder.withEmail(new Email(request.email()));
        }
        return userRepository.save(builder.build());
    }

    @Transactional
    @PreAuthorize("#userId == principal.user.id")
    public void updatePassword(Long userId, UpdatePasswordRequest request){
        if(!request.newPasswordsMatch()){
            throw new PasswordMismatchException("Passwords do not match");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if(!passwordEncoder.matches(request.currentPassword(), user.password().value())){
            throw new PasswordMismatchException("Current password does not match");
        }

        if(passwordEncoder.matches(request.newPassword(), user.password().value())){
           throw new PasswordMismatchException("New password cannot be the same as the current password");
        }

        var newEncodedPassword = passwordEncoder.encode(request.newPassword());
        User updateUser = user.mutate().withPassword(new Password(newEncodedPassword)).build();
        userRepository.save(updateUser);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') and #userId != principal.user.id")
    public User updateUserRole(Long userId, UserRole newRole){
        User user = getUserById(userId);
        User updatedUser = user.mutate().withRole(newRole).build();
        return userRepository.save(updatedUser);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public User activateUser(Long userId) {
        User user = getUserById(userId);
        if (user.isActive()) {
            throw new ForbiddenException("User is already active");
        }
        User activatedUser = user.mutate()
                .withActive(true)
                .build();
        return userRepository.save(activatedUser);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.user.id")
    public User deactivateUser(Long userId) {
        User user = getUserById(userId);

        if (!user.isActive()) {
            throw new ForbiddenException("User is already inactive");
        }

        User deactivatedUser = user.mutate()
                .withActive(false)
                .build();

        return userRepository.save(deactivatedUser);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') and #userId != principal.user.id")
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

}
