package com.hsf302.trainoffice.controller;

import com.hsf302.trainoffice.dto.ProfileForm;
import com.hsf302.trainoffice.entity.User;
import com.hsf302.trainoffice.repository.UserRepository;
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
    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
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

        user.setFullName(profileForm.getFullName().trim());
        user.setIdentityNumber(blankToNull(profileForm.getIdentityNumber()));
        user.setDateOfBirth(profileForm.getDateOfBirth());
        user.setGender(profileForm.getGender());
        userRepository.save(user);

        session.setAttribute("currentUser", user);
        redirectAttributes.addFlashAttribute("successMessage", "Cap nhat profile thanh cong.");
        return "redirect:/profile";
    }

    private User currentUser(HttpSession session) {
        Object sessionUser = session.getAttribute("currentUser");
        if (!(sessionUser instanceof User user) || user.getUserId() == null) {
            return null;
        }
        return userRepository.findById(user.getUserId()).orElse(null);
    }

    private ProfileForm toForm(User user) {
        ProfileForm form = new ProfileForm();
        form.setFullName(user.getFullName());
        form.setIdentityNumber(user.getIdentityNumber());
        form.setDateOfBirth(user.getDateOfBirth());
        form.setGender(user.getGender());
        return form;
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
