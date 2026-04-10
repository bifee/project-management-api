package com.bifee.projectmanagement.identity.api;

import com.bifee.projectmanagement.identity.application.AuthUserService;
import com.bifee.projectmanagement.identity.application.dto.auth.AuthenticationResponse;
import com.bifee.projectmanagement.identity.application.dto.auth.UserLoginRequest;
import com.bifee.projectmanagement.identity.application.dto.auth.UserRegistrationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {

    private final AuthUserService authService;

    public AuthController(AuthUserService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticate a user and return a JWT token")
    @ApiResponse(responseCode = "200", description = "User logged in successfully")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public AuthenticationResponse login(@RequestBody @Valid UserLoginRequest dto) {
        return authService.login(dto);
    }


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "User Registration", description = "Register a new user and return a JWT token")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "409", description = "Email already exists")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public AuthenticationResponse register(@RequestBody @Valid UserRegistrationRequest dto) {
        return authService.register(dto);
    }
}