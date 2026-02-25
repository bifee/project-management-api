package com.bifee.projectmanagement.identity.application;


import com.bifee.projectmanagement.identity.application.dto.auth.AuthenticationResponse;
import com.bifee.projectmanagement.identity.application.dto.auth.UserLoginRequest;
import com.bifee.projectmanagement.identity.application.dto.auth.UserRegistrationRequest;
import com.bifee.projectmanagement.identity.domain.User;
import com.bifee.projectmanagement.identity.domain.UserRepository;
import com.bifee.projectmanagement.identity.infrastructure.security.TokenService;
import com.bifee.projectmanagement.identity.infrastructure.security.UserDetailsImpl;
import com.bifee.projectmanagement.shared.DuplicateResourceException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthUserService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthUserService(AuthenticationManager authenticationManager, TokenService tokenService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public AuthenticationResponse login(UserLoginRequest dto){
        var authToken = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        var auth = authenticationManager.authenticate(authToken);
        var principal = auth.getPrincipal();
        if (!(principal instanceof UserDetailsImpl userDetails)) {
            throw new IllegalStateException("Unexpected principal type: " + (principal != null ? principal.getClass() : null));
        }
        var user = userDetails.user();
        String token = tokenService.generateToken(user);

        return new AuthenticationResponse(token, "Bearer", user.name());
    }

    public AuthenticationResponse register(UserRegistrationRequest dto) {
        if(userRepository.existsByEmail(dto.email())) {
            throw new DuplicateResourceException("User", "email", dto.email());
        }
        String encodedPassword = passwordEncoder.encode(dto.password());
        User userToSave = dto.toDomain(encodedPassword);
        User savedUser = userRepository.save(userToSave);
        String token = tokenService.generateToken(savedUser);
        return new AuthenticationResponse(token, "Bearer", savedUser.name());
    }
}
