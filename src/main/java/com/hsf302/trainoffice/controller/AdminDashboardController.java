package com.hsf302.trainoffice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class AdminDashboardController {

    @GetMapping({"/admin", "/admin/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("adminMenus", adminMenus());
        model.addAttribute("adminMenuUrls", adminMenuUrls());
        model.addAttribute("totalRevenue", "0 VND");
        model.addAttribute("totalBookings", 0);
        model.addAttribute("occupancyRate", "0%");
        model.addAttribute("pendingRefunds", 0);
        model.addAttribute("totalItems", 0);
        model.addAttribute("activeItems", 0);
        model.addAttribute("pendingItems", 0);
        model.addAttribute("issueItems", 0);
        model.addAttribute("recentBookings", List.of());
        model.addAttribute("trips", List.of());
        model.addAttribute("chartLabels", List.of());
        model.addAttribute("chartData", List.of());
        return "admin/dashboard";
    }

    private List<Map<String, String>> adminMenus() {
        return List.of(
                Map.of("key", "users", "label", "Users", "icon", " fas fa-users"),
                Map.of("key", "passengers", "label", "Passengers", "icon", " fas fa-user-friends"),
                Map.of("key", "stations", "label", "Stations", "icon", " fas fa-map-marker-alt"),
                Map.of("key", "routes", "label", "Routes", "icon", " fas fa-route"),
                Map.of("key", "trains", "label", "Trains", "icon", " fas fa-train"),
                Map.of("key", "coaches", "label", "Coaches", "icon", " fas fa-subway"),
                Map.of("key", "seats", "label", "Seats", "icon", " fas fa-chair"),
                Map.of("key", "trips", "label", "Trips", "icon", " fas fa-calendar-alt"),
                Map.of("key", "payments", "label", "Payments", "icon", " fas fa-credit-card")
        );
    }

    private Map<String, String> adminMenuUrls() {
        return Map.ofEntries(
                Map.entry("users", "/admin/users"),
                Map.entry("passengers", "/admin/passengers"),
                Map.entry("stations", "/admin/stations"),
                Map.entry("routes", "/admin/routes"),
                Map.entry("trains", "/admin/trains"),
                Map.entry("coaches", "/admin/coaches"),
                Map.entry("seats", "/admin/seats"),
                Map.entry("trips", "/admin/trips"),
                Map.entry("payments", "/admin/payments")
        );
    }
}
