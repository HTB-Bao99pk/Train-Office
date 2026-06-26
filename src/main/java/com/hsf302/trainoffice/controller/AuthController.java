package com.hsf302.trainoffice.controller;

import com.hsf302.trainoffice.dto.RegisterRequest;
import com.hsf302.trainoffice.entity.User;
import com.hsf302.trainoffice.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String pwd,
                        HttpSession session) {
        User user = userService.login(email, pwd);

        if (user == null) {
            return "auth/login";
        }

        session.setAttribute("currentUser", user);

        if (user.getRole().name().equalsIgnoreCase("ADMIN")) {
            return "redirect:/admin/dashboard";
        }

        if (user.getRole().name().equalsIgnoreCase("CUSTOMER")) {
            return "redirect:/booking/search";
        }
        return "auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String showRegister(Model model) {

        model.addAttribute(
                "registerRequest",
                new RegisterRequest());

        return "auth/register";
    }

    @PostMapping("/register")
    String register(@ModelAttribute RegisterRequest registerRequest, Model model) {
        if (userService.register(registerRequest)) {
            return "redirect:/login";
        } else {
            model.addAttribute("registerRequest", registerRequest);
            model.addAttribute("error", "Email da ton tai hoac thong tin dang ky khong hop le.");
            return "auth/register";
        }
    }

}
