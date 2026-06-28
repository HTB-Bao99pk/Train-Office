package com.hsf302.trainoffice.service.impl;

import com.hsf302.trainoffice.common.enums.UserRole;
import com.hsf302.trainoffice.common.enums.UserStatus;
import com.hsf302.trainoffice.dto.ProfileForm;
import com.hsf302.trainoffice.dto.RegisterRequest;
import com.hsf302.trainoffice.entity.User;
import com.hsf302.trainoffice.repository.UserRepository;
import com.hsf302.trainoffice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
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
        User user = userRepository.findByEmailAndPassword(email.trim().toLowerCase(), pwd);
        if (user == null || user.getStatus() != UserStatus.ACTIVE) {
            return null;
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> searchUsers(String keyword, UserRole role, UserStatus status) {
        return userRepository.search(blankToNull(keyword), role, status);
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
        validateUser(user);
        if (user.getUserId() != null) {
            userRepository.findById(user.getUserId()).ifPresent(existing -> {
                user.setCreatedAt(existing.getCreatedAt());
                if (user.getPassword() == null || user.getPassword().isBlank()) {
                    user.setPassword(existing.getPassword());
                }
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

    @Override
    public User updateProfile(Long userId, ProfileForm profileForm) {
        User user = getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setFullName(profileForm.getFullName().trim());
        user.setIdentityNumber(blankToNull(profileForm.getIdentityNumber()));
        user.setDateOfBirth(profileForm.getDateOfBirth());
        user.setGender(profileForm.getGender());
        return userRepository.save(user);
    }

    @Override
    public void resetPassword(String email, String password) {
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new RuntimeException("User not found");
        }

        user.setPassword(password);

        userRepository.save(user);
    }

    @Override
    public boolean changePassword(Long userId,
                                  String currentPassword,
                                  String newPassword) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return false;
        }

        if (!user.getPassword().equals(currentPassword)) {
            return false;
        }

        user.setPassword(newPassword);

        userRepository.save(user);

        return true;
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is required.");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }
        String email = user.getEmail().trim().toLowerCase();
        boolean duplicate = user.getUserId() == null
                ? userRepository.existsByEmail(email)
                : userRepository.existsByEmailAndUserIdNot(email, user.getUserId());
        if (duplicate) {
            throw new IllegalArgumentException("Email already exists.");
        }
        if (user.getUserId() == null && (user.getPassword() == null || user.getPassword().isBlank())) {
            throw new IllegalArgumentException("Password is required.");
        }
        if (user.getPassword() != null && user.getPassword().length() > 255) {
            throw new IllegalArgumentException("Password is too long.");
        }
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
