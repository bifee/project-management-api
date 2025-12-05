package com.bifee.projectmanagement.service;


import com.bifee.projectmanagement.dto.UserRegisterDTO;
import com.bifee.projectmanagement.entity.User;
import com.bifee.projectmanagement.entity.UserRole;
import com.bifee.projectmanagement.exception.UserAlreadyExistsException;
import com.bifee.projectmanagement.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        if(userRepository.existsByEmail(dto.getEmail())){
            throw new UserAlreadyExistsException("Email already exists");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(UserRole.DEV);
        user.setIsActive(true);

        return  userRepository.save(user);
    }

    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public boolean validatePassword(String raw, String encoded){
        return passwordEncoder.matches(raw, encoded);
    }
}
