package com.hsf302.trainoffice.service.impl;

import com.hsf302.trainoffice.common.enums.UserRole;
import com.hsf302.trainoffice.common.enums.UserStatus;
import com.hsf302.trainoffice.dto.RegisterRequest;
import com.hsf302.trainoffice.entity.User;
import com.hsf302.trainoffice.repository.UserRepository;
import com.hsf302.trainoffice.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean register(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String password = registerRequest.getPassword();
        String confirmPassword = registerRequest.getConfirmPassword();

        if (username == null || username.isBlank()) {
            return false;
        }

        if (password == null || password.isBlank()) {
            return false;
        }

        if (confirmPassword == null || !confirmPassword.equals(password)) {
            return false;
        }

        if (userRepository.existsByUsername(username)) {
            return false;
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(password);
        user.setRole(UserRole.CUSTOMER);
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);


        return true;
    }

    @Override
    public User login(String uname, String pwd) {
       if (userRepository.findByUsernameAndPasswordHash(uname, pwd) == null){
           return null;
       }
        return userRepository.findByUsernameAndPasswordHash(uname,pwd);
    }
}
