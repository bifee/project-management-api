package com.bifee.projectmanagement.old.service;


import com.bifee.projectmanagement.old.dto.UserRegisterDTO;
import com.bifee.projectmanagement.identity.domain.Password;
import com.bifee.projectmanagement.identity.domain.User;
import com.bifee.projectmanagement.old.exception.UserAlreadyExistsException;
import com.bifee.projectmanagement.old.repository.UserRepository;
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
    public User register(UserRegisterDTO dto){
        if(userRepository.existsByEmail(dto.email().value())){
            throw new UserAlreadyExistsException("Email already exists");
        }
        Password password = new Password(passwordEncoder.encode(dto.password().value()));
        User user = User.Builder.builder()
                .withName(dto.name())
                .withEmail(dto.email())
                .withPassword(password)
                .withRole(UserRole.DEV)
                .withActive(true).build();

        return  userRepository.save(user);
    }

    public boolean validatePassword(String raw, String encoded){
        return passwordEncoder.matches(raw, encoded);
    }
}
