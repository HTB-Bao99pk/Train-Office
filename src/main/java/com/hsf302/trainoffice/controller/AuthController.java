package com.hsf302.trainoffice.controller;

import com.hsf302.trainoffice.common.enums.UserRole;
import com.hsf302.trainoffice.dto.RegisterRequest;
import com.hsf302.trainoffice.entity.User;
import com.hsf302.trainoffice.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLogin(HttpSession session) {
        User currentUser = getLoggedInUser(session);

        if (currentUser != null) {
            return redirectByRole(currentUser, session);
        }

        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        Model model,
                        HttpSession session) {

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            model.addAttribute("error", "Please enter both email and password.");
            return "auth/login";
        }

        User user = userService.login(email, password);

        if (user == null) {
            model.addAttribute("error", "Email or password is incorrect, or the account is not active.");
            return "auth/login";
        }

        session.setAttribute("currentUser", user);

        // Giữ thêm userLogin để nếu có code nào copy từ Train-Ticket-Office vẫn dùng được.
        session.setAttribute("userLogin", user);

        return redirectByRole(user, session);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String showRegister(Model model, HttpSession session) {
        User currentUser = getLoggedInUser(session);

        if (currentUser != null) {
            return redirectByRole(currentUser, session);
        }

        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }

        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            bindingResult.rejectValue(
                    "confirmPassword",
                    "error.confirmPassword",
                    "Mật khẩu xác nhận không khớp"
            );
            return "auth/register";
        }

        if (userService.existsByEmail(registerRequest.getEmail())) {
            bindingResult.rejectValue(
                    "email",
                    "error.email",
                    "Email đã tồn tại"
            );
            return "auth/register";
        }

        boolean registered = userService.register(registerRequest);

        if (!registered) {
            model.addAttribute("error", "Không thể tạo tài khoản. Vui lòng kiểm tra lại thông tin đăng ký.");
            return "auth/register";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Đăng ký thành công. Vui lòng đăng nhập.");
        return "redirect:/login";
    }

    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "auth/forgot-password";
    }

    private User getLoggedInUser(HttpSession session) {
        Object sessionUser = session.getAttribute("currentUser");

        if (sessionUser instanceof User user) {
            return user;
        }

        return null;
    }

    private String redirectByRole(User user, HttpSession session) {
        String redirectAfterLogin = (String) session.getAttribute("redirectAfterLogin");

        if (isSafeRedirect(redirectAfterLogin)) {
            session.removeAttribute("redirectAfterLogin");
            return "redirect:" + redirectAfterLogin;
        }

        if (user.getRole() == UserRole.ADMIN) {
            return "redirect:/admin/dashboard";
        }

        return "redirect:/booking/search";
    }

    private boolean isSafeRedirect(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }

        String trimmed = url.trim();

        return trimmed.startsWith("/")
                && !trimmed.startsWith("//")
                && !trimmed.equals("/login")
                && !trimmed.equals("/register")
                && !trimmed.equals("/logout");
    }
}