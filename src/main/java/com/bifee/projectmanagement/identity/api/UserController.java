package com.bifee.projectmanagement.identity.api;


import com.bifee.projectmanagement.identity.application.UserService;
import com.bifee.projectmanagement.identity.application.dto.user.UpdatePasswordRequest;
import com.bifee.projectmanagement.identity.application.dto.user.UpdateUserProfileRequest;
import com.bifee.projectmanagement.identity.application.dto.user.UserResponse;
import com.bifee.projectmanagement.identity.domain.User;
import com.bifee.projectmanagement.identity.domain.UserRole;
import com.bifee.projectmanagement.identity.infrastructure.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse getMyProfile(@AuthenticationPrincipal UserDetailsImpl userDetails){
        Long userId = userDetails.user().id();
        return getUserById(userId);
    }

    @GetMapping("/{userId}")
    public UserResponse getUserById(@PathVariable Long userId){
        User user = userService.getUserById(userId);
        return UserResponse.from(user);
    }

    @GetMapping
    public List<UserResponse> getAllUsers(@AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        List<User> users = userService.getAllUsers(requesterId);
        return UserResponse.fromList(users);
    }

    @GetMapping("/active")
    public List<UserResponse> getActiveUsers(){
        List<User> users = userService.getActiveUsers();
        return UserResponse.fromList(users);
    }

    @GetMapping("/by-role")
    public List<UserResponse> getUsersByRole(@RequestParam UserRole role){
        List<User> users = userService.getUsersByRole(role);
        return UserResponse.fromList(users);
    }

    @GetMapping("/search")
    public List<UserResponse> searchUsersByName(@RequestParam String name){
        List<User> users = userService.getUsersByName(name);
        return UserResponse.fromList(users);
    }

    @PatchMapping("/me")
    public UserResponse updateMyProfile(@RequestBody @Valid UpdateUserProfileRequest request,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        User user = userService.updateProfile(requesterId, request, requesterId);
        return UserResponse.from(user);
    }

    @PatchMapping("/{userId}")
    public UserResponse updateProfile(@PathVariable Long userId,
                                        @RequestBody @Valid UpdateUserProfileRequest request,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        User user = userService.updateProfile(userId, request, requesterId);
        return UserResponse.from(user);
    }

    @PatchMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMyPassword(
            @RequestBody @Valid UpdatePasswordRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        userService.updatePassword(requesterId, request, requesterId);
    }

    @PatchMapping("/{userId}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassword(@PathVariable Long userId,
                                 @RequestBody @Valid UpdatePasswordRequest request,
                                 @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        userService.updatePassword(userId, request, requesterId);
    }

    @PatchMapping("/{userId}/role")
    public UserResponse updateUserRole(@PathVariable Long userId,
                                       @RequestParam UserRole newRole,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        User user = userService.updateUserRole(userId, newRole, requesterId);
        return UserResponse.from(user);
    }

    @PatchMapping("/{userId}/activate")
    public UserResponse activateUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long adminId = userDetails.user().id();
        User activated = userService.activateUser(userId, adminId);
        return UserResponse.from(activated);
    }

    @PatchMapping("/{userId}/deactivate")
    public UserResponse deactivateUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long requesterId = userDetails.user().id();
        User deactivated = userService.deactivateUser(userId, requesterId);
        return UserResponse.from(deactivated);
    }

    @PatchMapping("/me/deactivate")
    public UserResponse deactivateMyAccount(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.user().id();
        User deactivated = userService.deactivateUser(userId, userId);
        return UserResponse.from(deactivated);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long adminId = userDetails.user().id();
        userService.deleteUser(userId, adminId);
    }




}
