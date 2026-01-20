package com.bifee.projectmanagement.identity.application;

import com.bifee.projectmanagement.identity.application.dto.UserLoginRequest;
import com.bifee.projectmanagement.identity.application.dto.UserRegistrationRequest;
import com.bifee.projectmanagement.identity.domain.Email;
import com.bifee.projectmanagement.identity.domain.User;
import com.bifee.projectmanagement.identity.domain.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(UserRegistrationRequest dto) {
        Email email = new Email(dto.email());
        if(userRepository.existsByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }
        String encodedPassword = passwordEncoder.encode(dto.password());
        User userToSave = dto.toDomain(encodedPassword);
        return userRepository.save(userToSave);
    }

}
