package com.bifee.projectmanagement.identity.application;


import com.bifee.projectmanagement.identity.application.dto.AuthenticationResponse;
import com.bifee.projectmanagement.identity.application.dto.UserLoginRequest;
import com.bifee.projectmanagement.identity.domain.Email;
import com.bifee.projectmanagement.identity.domain.User;
import com.bifee.projectmanagement.identity.domain.UserRepository;
import com.bifee.projectmanagement.identity.infrastructure.security.TokenService;
import com.bifee.projectmanagement.identity.infrastructure.security.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthService(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public AuthenticationResponse login(UserLoginRequest dto){
        var authToken = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        var auth = authenticationManager.authenticate(authToken);

        var user = ((UserDetailsImpl) auth.getPrincipal()).user();
        String token = tokenService.generateToken(user);

        return new AuthenticationResponse(token, "Bearer", user.name());
    }
}
