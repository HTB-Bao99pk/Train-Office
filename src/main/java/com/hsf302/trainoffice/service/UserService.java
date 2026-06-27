package com.hsf302.trainoffice.service;

import com.hsf302.trainoffice.dto.RegisterRequest;
import com.hsf302.trainoffice.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    boolean register(RegisterRequest registerRequest);

    User login(String email, String pwd);

    List<User> getAllUsers();

    Optional<User> getUserById(Long userId);

    User saveUser(User user);

    void deleteUser(Long userId);
}
