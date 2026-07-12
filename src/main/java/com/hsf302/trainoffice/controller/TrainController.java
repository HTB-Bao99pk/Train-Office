package com.hsf302.trainoffice.controller;

import com.hsf302.trainoffice.common.enums.TrainStatus;
import com.hsf302.trainoffice.config.AdminErrorMessageUtil;
import com.hsf302.trainoffice.entity.Train;
import com.hsf302.trainoffice.service.TrainService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/trains")
public class TrainController {

    private final TrainService trainService;

    @Autowired
    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    private void addCommonAttributes(Model model) {
        model.addAttribute("allTrainStatus", TrainStatus.values());
    }

    @GetMapping
    public String listTrains(
            Model model,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        int safePage = Math.max(page, 1);
        String cleanKeyword = normalizeKeyword(keyword);

        Page<Train> trainPage = trainService.listAll(safePage, cleanKeyword);

        model.addAttribute("trains", trainPage.getContent());
        model.addAttribute("currentPage", safePage);
        model.addAttribute("totalPages", trainPage.getTotalPages());
        model.addAttribute("totalItems", trainPage.getTotalElements());
        model.addAttribute("keyword", cleanKeyword);

        return "trains/admin-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("train", new Train());
        addCommonAttributes(model);

        return "trains/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        Optional<Train> train = trainService.getTrainById(id);

        if (train.isPresent()) {
            model.addAttribute("train", train.get());
            addCommonAttributes(model);

            return "trains/form";
        }

        redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Train was not found."
        );

        return "redirect:/admin/trains";
    }

    @PostMapping("/save")
    public String saveTrain(@Valid @ModelAttribute("train") Train train,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            addCommonAttributes(model);

            return "trains/form";
        }

        try {
            trainService.saveTrain(train);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Train saved successfully."
            );

            return "redirect:/admin/trains";

        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            addCommonAttributes(model);

            return "trains/form";

        } catch (Exception e) {
            model.addAttribute(
                    "errorMessage",
                    "Cannot save this train. Please check the information and try again."
            );
            addCommonAttributes(model);

            return "trains/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteTrain(@PathVariable("id") Long id,
                              RedirectAttributes redirectAttributes) {
        try {
            trainService.deleteTrain(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Train with ID " + id + " has been deleted."
            );

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    AdminErrorMessageUtil.deleteMessage("train", e)
            );
        }

        return "redirect:/admin/trains";
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        return keyword.trim();
    }
}