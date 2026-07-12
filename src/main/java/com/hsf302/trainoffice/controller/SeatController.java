package com.hsf302.trainoffice.controller;

import com.hsf302.trainoffice.entity.Coach;
import com.hsf302.trainoffice.entity.Seat;
import com.hsf302.trainoffice.service.CoachService;
import com.hsf302.trainoffice.service.SeatService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/seats")
public class SeatController {

    private final SeatService seatService;
    private final CoachService coachService;

    public SeatController(SeatService seatService, CoachService coachService) {
        this.seatService = seatService;
        this.coachService = coachService;
    }

    @GetMapping
    public String listSeats(Model model,
                            @RequestParam(value = "coachId", required = false) Long coachId,
                            @RequestParam(value = "keyword", required = false) String keyword,
                            @RequestParam(value = "page", defaultValue = "1") int page) {

        int safePage = Math.max(page, 1);
        String cleanKeyword = normalizeKeyword(keyword);

        List<Coach> allCoaches = coachService.getAllCoaches();
        Coach selectedCoach = null;

        if (coachId != null) {
            selectedCoach = allCoaches.stream()
                    .filter(coach -> coach.getCoachId().equals(coachId))
                    .findFirst()
                    .orElse(null);
        }

        Page<Seat> seatPage = seatService.listAll(safePage, cleanKeyword, coachId);

        model.addAttribute("seats", seatPage.getContent());
        model.addAttribute("allCoaches", allCoaches);
        model.addAttribute("selectedCoachId", coachId);
        model.addAttribute("selectedCoach", selectedCoach);
        model.addAttribute("keyword", cleanKeyword);

        model.addAttribute("currentPage", safePage);
        model.addAttribute("totalPages", seatPage.getTotalPages());
        model.addAttribute("totalItems", seatPage.getTotalElements());

        return "seats/admin-list";
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        return keyword.trim();
    }
}