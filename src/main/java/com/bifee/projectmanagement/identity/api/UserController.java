package com.bifee.projectmanagement.identity.api;


import com.bifee.projectmanagement.identity.application.UserService;
import com.bifee.projectmanagement.identity.application.dto.user.UpdatePasswordRequest;
import com.bifee.projectmanagement.identity.application.dto.user.UpdateUserProfileRequest;
import com.bifee.projectmanagement.identity.application.dto.user.UserResponse;
import com.bifee.projectmanagement.identity.domain.User;
import com.bifee.projectmanagement.identity.domain.UserRole;
import com.bifee.projectmanagement.identity.infrastructure.security.SecurityConfig;
import com.bifee.projectmanagement.identity.infrastructure.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Endpoints for managing users")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get My Profile", description = "Retrieve the profile information of the currently authenticated user")
    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - user is not authenticated")
    @ApiResponse(responseCode = "403", description = "Forbidden - user does not have permission to access this resource")
    @ApiResponse(responseCode = "500", description = "Server Error")
    public UserResponse getMyProfile(@AuthenticationPrincipal UserDetailsImpl userDetails){
        Long userId = userDetails.user().id();
        return getUserById(userId);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get User Profile", description = "Retrieve the profile information of a specific user")
    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "500", description = "Server Error")
    public UserResponse getUserById(@PathVariable Long userId){
        User user = userService.getUserById(userId);
        return UserResponse.from(user);
    }

    @GetMapping
    @Operation(summary = "Get All Users", description = "Retrieve a list of all users")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - user does not have permission to access this resource")
    @ApiResponse(responseCode = "500", description = "Server Error")
    public List<UserResponse> getAllUsers(@AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        System.out.println("Requester ID: " + requesterId);
        List<User> users = userService.getAllUsers(requesterId);
        System.out.println("Users retrieved: " + users.size());
        return UserResponse.fromList(users);
    }

    @GetMapping("/active")
    @Operation(summary = "Get Active Users", description = "Retrieve a list of all active users")
    @ApiResponse(responseCode = "200", description = "Active users retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - user does not have permission to access this resource")
    @ApiResponse(responseCode = "500", description = "Server Error")
    public List<UserResponse> getActiveUsers(@AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        List<User> users = userService.getActiveUsers(requesterId);
        return UserResponse.fromList(users);
    }

    @GetMapping("/by-role")
    @Operation(summary = "Get Users by Role", description = "Retrieve a list of users based on their role")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - user does not have permission to access this resource")
    @ApiResponse(responseCode = "500", description = "Server Error")
    public List<UserResponse> getUsersByRole(@RequestParam UserRole role, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        List<User> users = userService.getUsersByRole(requesterId, role);
        return UserResponse.fromList(users);
    }

    @GetMapping("/search")
    @Operation(summary = "Search Users by Name", description = "Retrieve a list of users based on their name")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - user does not have permission to access this resource")
    @ApiResponse(responseCode = "500", description = "Server Error")
    public List<UserResponse> searchUsersByName(@RequestParam String name, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        List<User> users = userService.getUsersByName(requesterId, name);
        return UserResponse.fromList(users);
    }

    @PatchMapping("/me")
    @Operation(summary = "Update My Profile", description = "Update the profile information of the currently authenticated user")
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "500", description = "Server Error")
    public UserResponse updateMyProfile(@RequestBody @Valid UpdateUserProfileRequest request,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        User user = userService.updateProfile(requesterId, request, requesterId);
        return UserResponse.from(user);
    }

    @PatchMapping("/{userId}")
    @Operation(summary = "Update User Profile", description = "Update the profile information of a specific user")
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "403", description = "Forbidden - user does not have permission to update this profile")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "500", description = "Server Error")
    public UserResponse updateProfile(@PathVariable Long userId,
                                        @RequestBody @Valid UpdateUserProfileRequest request,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        User user = userService.updateProfile(userId, request, requesterId);
        return UserResponse.from(user);
    }

    @PatchMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update My Password", description = "Update the password of the currently authenticated user")
    @ApiResponse(responseCode = "204", description = "Password updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "403", description = "Forbidden - user does not have permission to update this password")
    @ApiResponse(responseCode = "500", description = "Server Error")
    public void updateMyPassword(
            @RequestBody @Valid UpdatePasswordRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        userService.updatePassword(requesterId, request, requesterId);
    }

    @PatchMapping("/{userId}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update User Password", description = "Update the password of a specific user")
    @ApiResponse(responseCode = "204", description = "Password updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "403", description = "Forbidden - user does not have permission to update this password")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "500", description = "Server Error")
    public void updatePassword(@PathVariable Long userId,
                                 @RequestBody @Valid UpdatePasswordRequest request,
                                 @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        userService.updatePassword(userId, request, requesterId);
    }

    @PatchMapping("/{userId}/role")
    @Operation(summary = "Update User Role", description = "Update the role of a specific user")
    @ApiResponse(responseCode = "200", description = "Role updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "403", description = "Forbidden - user does not have permission to update this role")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "500", description = "Server Error")
    public UserResponse updateUserRole(@PathVariable Long userId,
                                       @RequestParam UserRole newRole,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long requesterId = userDetails.user().id();
        User user = userService.updateUserRole(userId, newRole, requesterId);
        return UserResponse.from(user);
    }

    @PatchMapping("/{userId}/activate")
    @Operation(summary = "Activate User", description = "Activate a user")
    @ApiResponse(responseCode = "200", description = "User activated successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - user does not have permission to activate this user")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "500", description = "Server Error")
    public UserResponse activateUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long adminId = userDetails.user().id();
        User activated = userService.activateUser(userId, adminId);
        return UserResponse.from(activated);
    }

    @PatchMapping("/{userId}/deactivate")
    @Operation(summary = "Deactivate User", description = "Deactivate a user")
    @ApiResponse(responseCode = "200", description = "User deactivated successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - user does not have permission to deactivate this user")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "500", description = "Server Error")
    public UserResponse deactivateUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long requesterId = userDetails.user().id();
        User deactivated = userService.deactivateUser(userId, requesterId);
        return UserResponse.from(deactivated);
    }

    @PatchMapping("/me/deactivate")
    @Operation(summary = "Deactivate My Account", description = "Deactivate the currently authenticated user's account")
    @ApiResponse(responseCode = "200", description = "Account deactivated successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - user does not have permission to deactivate their account")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "500", description = "Server Error")
    public UserResponse deactivateMyAccount(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.user().id();
        User deactivated = userService.deactivateUser(userId, userId);
        return UserResponse.from(deactivated);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete User", description = "Delete a user")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - user does not have permission to delete this user")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "500", description = "Server Error")
    public void deleteUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long adminId = userDetails.user().id();
        userService.deleteUser(userId, adminId);
    }




}
