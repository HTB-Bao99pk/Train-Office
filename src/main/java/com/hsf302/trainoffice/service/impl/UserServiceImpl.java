package com.hsf302.trainoffice.service.impl;

import com.hsf302.trainoffice.common.enums.UserRole;
import com.hsf302.trainoffice.common.enums.UserStatus;
import com.hsf302.trainoffice.dto.RegisterRequest;
import com.hsf302.trainoffice.entity.User;
import com.hsf302.trainoffice.repository.UserRepository;
import com.hsf302.trainoffice.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean register(RegisterRequest registerRequest) {
        String fullName = registerRequest.getFullName();
        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();
        String confirmPassword = registerRequest.getConfirmPassword();

        if (fullName == null || fullName.isBlank()) {
            return false;
        }

        if (email == null || email.isBlank()) {
            return false;
        }

        email = email.trim().toLowerCase();

        if (password == null || password.isBlank()) {
            return false;
        }

        if (confirmPassword == null || !confirmPassword.equals(password)) {
            return false;
        }
        if (!registerRequest.getPassword()
                .equals(registerRequest.getConfirmPassword())) {
            return false;
        }


        User user = new User();

        if (userRepository.existsByEmail(email)) {
            return false;
        }

        user.setEmail(email);

        user.setPassword(
                registerRequest.getPassword());

        user.setFullName(registerRequest.getFullName().trim());

        user.setRole(UserRole.CUSTOMER);

        user.setStatus(UserStatus.ACTIVE);

        try {
            userRepository.save(user);
            return true;
        } catch (DataIntegrityViolationException ex) {
            return false;
        }
    }

    @Override
    public User login(String email, String pwd) {
       if (email == null || pwd == null) {
           return null;
       }
        return userRepository.findByEmailAndPassword(email.trim().toLowerCase(), pwd);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }
        return userRepository.findById(userId);
    }

    @Override
    public User saveUser(User user) {
        if (user.getUserId() != null) {
            userRepository.findById(user.getUserId()).ifPresent(existing -> {
                user.setCreatedAt(existing.getCreatedAt());
            });
        }
        if (user.getEmail() != null) {
            user.setEmail(user.getEmail().trim().toLowerCase());
        }
        if (user.getRole() == null) {
            user.setRole(UserRole.CUSTOMER);
        }
        if (user.getStatus() == null) {
            user.setStatus(UserStatus.ACTIVE);
        }
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
