package com.hsf302.trainoffice.controller;

import com.hsf302.trainoffice.common.enums.UserRole;
import com.hsf302.trainoffice.common.enums.UserStatus;
import com.hsf302.trainoffice.entity.User;
import com.hsf302.trainoffice.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users/list";
    }

    @GetMapping({"/new", "/create"})
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        addFormOptions(model);
        return "users/form";
    }

    @GetMapping({"/edit/{id}", "/{id}/edit"})
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return userService.getUserById(id)
                .map(user -> {
                    model.addAttribute("user", user);
                    addFormOptions(model);
                    return "users/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
                    return "redirect:/admin/users";
                });
    }

    @GetMapping("/{id}")
    public String showDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return userService.getUserById(id)
                .map(user -> {
                    model.addAttribute("user", user);
                    return "users/detail";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
                    return "redirect:/admin/users";
                });
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("successMessage", "User saved successfully.");
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully.");
        return "redirect:/admin/users";
    }

    private void addFormOptions(Model model) {
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("statuses", UserStatus.values());
    }
}
