package com.bifee.projectmanagement.identity.api;

import com.bifee.projectmanagement.identity.application.AuthUserService;
import com.bifee.projectmanagement.identity.application.dto.AuthenticationResponse;
import com.bifee.projectmanagement.identity.application.dto.UserLoginRequest;
import com.bifee.projectmanagement.identity.application.dto.UserRegistrationRequest;
import com.bifee.projectmanagement.identity.domain.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthUserService authService;

    public AuthController(AuthUserService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody @Valid UserLoginRequest dto) {
        return authService.login(dto);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthenticationResponse register(@RequestBody @Valid UserRegistrationRequest dto) {
        return authService.register(dto);
    }
}