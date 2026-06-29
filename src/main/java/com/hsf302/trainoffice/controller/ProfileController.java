package com.hsf302.trainoffice.controller;

import com.hsf302.trainoffice.dto.ProfileForm;
import com.hsf302.trainoffice.dto.ChangePasswordRequest;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {
    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        User user = currentUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("currentUser", user);
        if (!model.containsAttribute("profileForm")) {
            model.addAttribute("profileForm", toForm(user));
        }
        return "customer/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profileForm") ProfileForm profileForm,
                                BindingResult bindingResult,
                                HttpSession session,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        User user = currentUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", user);
            model.addAttribute("errorMessage", "Vui long kiem tra lai thong tin profile.");
            return "customer/profile";
        }

        user = userService.updateProfile(user.getUserId(), profileForm);

        session.setAttribute("currentUser", user);
        redirectAttributes.addFlashAttribute("successMessage", "Cap nhat profile thanh cong.");
        return "redirect:/profile";
    }

    @GetMapping("/change-password")
    public String showChangePassword(Model model){

        model.addAttribute(
                "changePasswordForm",
                new ChangePasswordRequest());

        return "customer/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @Valid
            @ModelAttribute("changePasswordForm")
            ChangePasswordRequest request,
            BindingResult bindingResult,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            return "redirect:/login";
        }
        // Lỗi validate (@NotBlank, @Size,...)
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage",
                    "Please correct the highlighted errors.");
            return "customer/change-password";
        }

        // Confirm password không khớp
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {

            bindingResult.rejectValue(
                    "confirmNewPassword",
                    "error.confirmNewPassword",
                    "Confirm password does not match.");

            model.addAttribute("errorMessage",
                    "Confirm password does not match.");

            return "customer/change-password";
        }

        // Password mới trùng password cũ
        if (request.getCurrentPassword().equals(request.getNewPassword())) {

            bindingResult.rejectValue(
                    "newPassword",
                    "error.newPassword",
                    "New password must be different from current password.");

            model.addAttribute("errorMessage",
                    "New password must be different from current password.");

            return "customer/change-password";
        }

        boolean success = userService.changePassword(
                user.getUserId(),
                request.getCurrentPassword(),
                request.getNewPassword());

        // Sai password hiện tại
        if (!success) {

            bindingResult.rejectValue(
                    "currentPassword",
                    "error.currentPassword",
                    "Current password is incorrect.");

            model.addAttribute("errorMessage",
                    "Current password is incorrect.");

            return "customer/change-password";
        }

        // Cập nhật session
        session.setAttribute("currentUser",
                userService.findById(user.getUserId()));

        // Thành công
        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Password changed successfully.");

        return "redirect:/change-password";
    }

    private User currentUser(HttpSession session) {
        Object sessionUser = session.getAttribute("currentUser");
        if (!(sessionUser instanceof User user) || user.getUserId() == null) {
            return null;
        }
        return userService.getUserById(user.getUserId()).orElse(null);
    }

    private ProfileForm toForm(User user) {
        ProfileForm form = new ProfileForm();
        form.setFullName(user.getFullName());
        form.setIdentityNumber(user.getIdentityNumber());
        form.setDateOfBirth(user.getDateOfBirth());
        form.setGender(user.getGender());
        return form;
    }

}
