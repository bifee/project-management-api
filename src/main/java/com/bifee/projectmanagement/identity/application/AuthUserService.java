package com.bifee.projectmanagement.identity.application;


import com.bifee.projectmanagement.identity.application.dto.AuthenticationResponse;
import com.bifee.projectmanagement.identity.application.dto.UserLoginRequest;
import com.bifee.projectmanagement.identity.application.dto.UserRegistrationRequest;
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
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
        }
        var user = userDetails.user();
        String token = tokenService.generateToken(user);

        return new AuthenticationResponse(token, "Bearer", user.name());
    }

    public User register(UserRegistrationRequest dto) {
        Email email = new Email(dto.email());
        if(userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }
        String encodedPassword = passwordEncoder.encode(dto.password());
        User userToSave = dto.toDomain(encodedPassword);
        return userRepository.save(userToSave);
    }
}
