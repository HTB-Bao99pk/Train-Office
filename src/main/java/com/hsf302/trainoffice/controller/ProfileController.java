package com.hsf302.trainoffice.controller;

import com.hsf302.trainoffice.dto.ProfileForm;
import com.hsf302.trainoffice.entity.Passenger;
import com.hsf302.trainoffice.entity.User;
import com.hsf302.trainoffice.repository.PassengerRepository;
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
    private final PassengerRepository passengerRepository;

    public ProfileController(UserRepository userRepository, PassengerRepository passengerRepository) {
        this.userRepository = userRepository;
        this.passengerRepository = passengerRepository;
    }

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        User user = currentUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("currentUser", user);
        if (!model.containsAttribute("profileForm")) {
            model.addAttribute("profileForm", toForm(findPassenger(user)));
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

        Passenger passenger = findPassenger(user);
        passenger.setUser(user);
        passenger.setFullName(profileForm.getFullName().trim());
        passenger.setIdentityNumber(blankToNull(profileForm.getIdentityNumber()));
        passenger.setDateOfBirth(profileForm.getDateOfBirth());
        passenger.setGender(profileForm.getGender());
        passengerRepository.save(passenger);

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

    private Passenger findPassenger(User user) {
        return passengerRepository.findFirstByUser_UserId(user.getUserId())
                .orElseGet(Passenger::new);
    }

    private ProfileForm toForm(Passenger passenger) {
        ProfileForm form = new ProfileForm();
        form.setPassengerId(passenger.getPassengerId());
        form.setFullName(passenger.getFullName());
        form.setIdentityNumber(passenger.getIdentityNumber());
        form.setDateOfBirth(passenger.getDateOfBirth());
        form.setGender(passenger.getGender());
        return form;
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
