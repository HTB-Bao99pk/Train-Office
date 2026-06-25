package com.hsf302.trainoffice.controller;

import com.hsf302.trainoffice.dto.RegisterRequest;
import com.hsf302.trainoffice.entity.User;
import com.hsf302.trainoffice.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }


    //Login !!!!! ======================
    @GetMapping("/login")
    public String showLogin() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String uname, @RequestParam("password") String pwd) {
        User user = userService.login(uname, pwd);

        if (user == null) {
            return "auth/login";
        }

        if (user.getRole().name().equalsIgnoreCase("ADMIN")) {
            return "redirect:admin/dashboard";
        }

        if (user.getRole().name().equalsIgnoreCase("CUSTOMER")) {
            return "redirect:/booking/search";
        }
        return "auth/login";
    }
    // End Login =======================
    // Register !!!!! ==================
    @GetMapping("/register")
    String showRegister() {
        return "auth/register";
    }
    @PostMapping("/register")
    String register(@ModelAttribute RegisterRequest registerRequest) {
        if (userService.register(registerRequest)) {
            return "auth/login";
        } else {
            return "auth/register";
        }


    }
    // End Register ====================


}
