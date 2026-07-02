package com.hsf302.trainoffice.controller;

import com.hsf302.trainoffice.entity.Coach;
import com.hsf302.trainoffice.entity.Seat;
import com.hsf302.trainoffice.service.CoachService;
import com.hsf302.trainoffice.service.SeatService;
import com.hsf302.trainoffice.config.AdminErrorMessageUtil;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

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
                            @RequestParam(value = "coachId", required = false) Long coachId) {

        List<Seat> seats;
        List<Coach> allCoaches = coachService.getAllCoaches();
        Coach selectedCoach = null;

        if (coachId != null) {
            Optional<Coach> coachOpt = allCoaches.stream()
                    .filter(coach -> coach.getCoachId().equals(coachId))
                    .findFirst();

            if (coachOpt.isPresent()) {
                selectedCoach = coachOpt.get();
                seats = selectedCoach.getSeats();
            } else {
                seats = List.of();
            }
        } else {
            seats = seatService.getAllSeats();
        }

        model.addAttribute("seats", seats);
        model.addAttribute("allCoaches", allCoaches);
        model.addAttribute("selectedCoachId", coachId);
        model.addAttribute("selectedCoach", selectedCoach);

        return "seats/admin-list";
    }

    private void addCommonAttributes(Model model, Long coachId) {
        if (coachId != null) {
            model.addAttribute(
                    "allCoaches",
                    coachService.getCoachById(coachId).stream().toList()
            );
            model.addAttribute("selectedCoachId", coachId);
        } else {
            model.addAttribute("allCoaches", coachService.getAllCoaches());
        }
    }

    @GetMapping("/new")
    public String showCreateForm(Model model,
                                 @RequestParam("coachId") Long coachId,
                                 RedirectAttributes redirectAttributes) {

        Optional<Coach> coachOpt = coachService.getCoachById(coachId);

        if (coachOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Coach was not found."
            );
            return "redirect:/admin/seats";
        }

        Seat newSeat = new Seat();
        newSeat.setCoach(coachOpt.get());

        model.addAttribute("seat", newSeat);
        addCommonAttributes(model, coachId);

        return "seats/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        Optional<Seat> seatOpt = seatService.getSeatById(id);

        if (seatOpt.isPresent()) {
            Seat seat = seatOpt.get();

            model.addAttribute("seat", seat);

            if (seat.getCoach() != null) {
                addCommonAttributes(model, seat.getCoach().getCoachId());
            } else {
                addCommonAttributes(model, null);
            }

            return "seats/form";
        }

        redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Seat was not found."
        );

        return "redirect:/admin/seats";
    }

    @PostMapping("/save")
    public String saveSeat(@Valid @ModelAttribute("seat") Seat seat,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        Long coachId = null;

        if (seat.getCoach() != null) {
            coachId = seat.getCoach().getCoachId();
        }

        if (result.hasErrors()) {
            addCommonAttributes(model, coachId);
            return "seats/form";
        }

        try {
            seatService.saveSeat(seat);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Seat saved successfully."
            );

            if (coachId != null) {
                return "redirect:/admin/seats?coachId=" + coachId;
            }

            return "redirect:/admin/seats";

        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            addCommonAttributes(model, coachId);
            return "seats/form";

        } catch (Exception e) {
            model.addAttribute(
                    "errorMessage",
                    "Cannot save this seat. Please check the information and try again."
            );
            addCommonAttributes(model, coachId);
            return "seats/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteSeat(@PathVariable("id") Long id,
                             RedirectAttributes redirectAttributes,
                             @RequestParam(value = "coachId", required = false) Long coachId) {

        try {
            seatService.deleteSeat(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Seat with ID " + id + " has been deleted."
            );

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    AdminErrorMessageUtil.deleteMessage("seat", e)
            );
        }

        if (coachId != null) {
            return "redirect:/admin/seats?coachId=" + coachId;
        }

        return "redirect:/admin/seats";
    }
}