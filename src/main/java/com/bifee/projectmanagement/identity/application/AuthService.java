package com.bifee.projectmanagement.identity.application;


import com.bifee.projectmanagement.identity.application.dto.AuthenticationResponse;
import com.bifee.projectmanagement.identity.application.dto.UserLoginRequest;
import com.bifee.projectmanagement.identity.domain.Email;
import com.bifee.projectmanagement.identity.domain.User;
import com.bifee.projectmanagement.identity.domain.UserRepository;
import com.bifee.projectmanagement.identity.infrastructure.security.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = new TokenService();
    }

    public AuthenticationResponse login(UserLoginRequest dto){
        Email email = new Email(dto.email());

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Invalid Email/Password"));
        if(!passwordEncoder.matches(dto.password(), user.password().value())){
            throw new RuntimeException("Invalid Email/Password");
        }
        String token = tokenService.generateToken(user);

        return new AuthenticationResponse(token, "bearer");

    }
}
