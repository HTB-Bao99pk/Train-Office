package com.hsf302.trainoffice.service.impl;

import com.hsf302.trainoffice.common.enums.UserRole;
import com.hsf302.trainoffice.common.enums.UserStatus;
import com.hsf302.trainoffice.dto.RegisterRequest;
import com.hsf302.trainoffice.entity.Passenger;
import com.hsf302.trainoffice.entity.User;
import com.hsf302.trainoffice.repository.PassengerRepository;
import com.hsf302.trainoffice.repository.UserRepository;
import com.hsf302.trainoffice.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PassengerRepository passengerRepository;

    public UserServiceImpl(UserRepository userRepository, PassengerRepository passengerRepository) {
        this.userRepository = userRepository;
        this.passengerRepository = passengerRepository;
    }

    @Override
    public boolean register(RegisterRequest registerRequest) {
        String fullName = registerRequest.getFullName();
        String username = registerRequest.getUsername();
        String password = registerRequest.getPassword_hash();
        String confirmPassword = registerRequest.getConfirmPassword();

        if (userRepository.existsByUsername(username)) {
            return false;
        }

        if (fullName == null || fullName.isBlank()) {
            return false;
        }

        if (username == null || username.isBlank()) {
            return false;
        }

        if (password == null || password.isBlank()) {
            return false;
        }

        if (confirmPassword == null || !confirmPassword.equals(password)) {
            return false;
        }
        if (!registerRequest.getPassword_hash()
                .equals(registerRequest.getConfirmPassword())) {
            return false;
        }


        User user = new User();

        user.setUsername(registerRequest.getUsername());

        user.setPasswordHash(
                registerRequest.getPassword_hash());

        user.setRole(UserRole.CUSTOMER);

        user.setStatus(UserStatus.ACTIVE);

        user = userRepository.save(user);

        Passenger passenger = new Passenger();

        passenger.setFullName(
                registerRequest.getFullName());

        passengerRepository.save(passenger);
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
