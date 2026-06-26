package com.hsf302.trainoffice.service;

import com.hsf302.trainoffice.dto.RegisterRequest;
import com.hsf302.trainoffice.entity.User;

public interface UserService {
    boolean register(RegisterRequest registerRequest);

    User login(String email, String pwd);
}
