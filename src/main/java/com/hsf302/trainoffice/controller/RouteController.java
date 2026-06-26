package com.hsf302.trainoffice.controller;

import com.hsf302.trainoffice.entity.Route;
import com.hsf302.trainoffice.service.RouteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    // ==========================
    // LIST
    // ==========================
    @GetMapping
    public String listRoutes(Model model) {

        model.addAttribute("routes", routeService.getAllRoutes());

        return "route/list";
    }

    // ==========================
    // CREATE
    // ==========================
    @GetMapping("/new")
    public String showCreateForm(Model model) {

        model.addAttribute("route", new Route());

        return "route/form";
    }

    // ==========================
    // EDIT
    // ==========================
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id,
                               Model model) {

        routeService.getRouteById(id).ifPresent(route ->
                model.addAttribute("route", route));

        return model.containsAttribute("route")
                ? "route/form"
                : "redirect:/routes";
    }

    // ==========================
    // SAVE
    // ==========================
    @PostMapping("/save")
    public String saveRoute(
            @Valid @ModelAttribute("route") Route route,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "route/form";
        }

        try {

            routeService.saveRoute(route);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Route saved successfully!"
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/routes";
    }

    // ==========================
    // DELETE
    // ==========================
    @GetMapping("/delete/{id}")
    public String deleteRoute(@PathVariable Long id,
                              RedirectAttributes redirectAttributes) {

        try {

            routeService.deleteRoute(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Route deleted successfully!"
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/routes";
    }

}