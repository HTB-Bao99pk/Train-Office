package com.hsf302.trainoffice.controller;

import com.hsf302.trainoffice.entity.Coach;
import com.hsf302.trainoffice.entity.Train;
import com.hsf302.trainoffice.service.CoachService;
import com.hsf302.trainoffice.service.TrainService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/coaches")
public class CoachController {

    private final CoachService coachService;
    private final TrainService trainService;

    public CoachController(CoachService coachService, TrainService trainService) {
        this.coachService = coachService;
        this.trainService = trainService;
    }

    @GetMapping
    public String listCoaches(
            Model model,
            @RequestParam(value = "trainId", required = false) Long trainId
    ) {
        List<Coach> coaches;
        List<Train> allTrains = trainService.getAllTrains();
        Train selectedTrain = null;

        if (trainId != null) {
            Optional<Train> trainOpt = allTrains.stream()
                    .filter(train -> train.getTrainId().equals(trainId))
                    .findFirst();

            if (trainOpt.isPresent()) {
                selectedTrain = trainOpt.get();
                coaches = selectedTrain.getCoaches();
            } else {
                coaches = List.of();
            }
        } else {
            coaches = coachService.getAllCoaches();
        }

        model.addAttribute("coaches", coaches);
        model.addAttribute("allTrains", allTrains);
        model.addAttribute("selectedTrainId", trainId);
        model.addAttribute("selectedTrain", selectedTrain);

        return "coaches/list";
    }

    private void addCommonAttributes(Model model, Long trainId) {
        if (trainId != null) {
            model.addAttribute("allTrains", trainService.getTrainById(trainId).stream().toList());
            model.addAttribute("selectedTrainId", trainId);
        } else {
            model.addAttribute("allTrains", trainService.getAllTrains());
        }
    }

    @GetMapping("/new")
    public String showCreateForm(
            Model model,
            @RequestParam(value = "trainId", required = false) Long trainId
    ) {
        Coach coach = new Coach();

        if (trainId != null) {
            trainService.getTrainById(trainId).ifPresent(coach::setTrain);
        }

        model.addAttribute("coach", coach);
        addCommonAttributes(model, trainId);

        return "coaches/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Optional<Coach> coachOpt = coachService.getCoachById(id);

        if (coachOpt.isPresent()) {
            Coach coach = coachOpt.get();

            model.addAttribute("coach", coach);
            addCommonAttributes(model, coach.getTrain().getTrainId());

            return "coaches/form";
        }

        return "redirect:/coaches";
    }

    @PostMapping("/save")
    public String saveCoach(
            @Valid @ModelAttribute("coach") Coach coach,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Long trainId = coach.getTrain() != null ? coach.getTrain().getTrainId() : null;

        if (result.hasErrors()) {
            addCommonAttributes(model, trainId);
            return "coaches/form";
        }

        try {
            coachService.saveCoach(coach);
            redirectAttributes.addFlashAttribute("successMessage", "Coach saved successfully!");

            if (trainId != null) {
                return "redirect:/coaches?trainId=" + trainId;
            }

            return "redirect:/coaches";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error saving coach: " + e.getMessage());
            addCommonAttributes(model, trainId);

            return "coaches/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteCoach(
            @PathVariable("id") Long id,
            @RequestParam(value = "trainId", required = false) Long trainId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            coachService.deleteCoach(id);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Coach with ID " + id + " has been deleted."
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Error deleting coach: " + e.getMessage()
            );
        }

        if (trainId != null) {
            return "redirect:/coaches?trainId=" + trainId;
        }

        return "redirect:/coaches";
    }
}