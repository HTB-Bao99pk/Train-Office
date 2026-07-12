package com.hsf302.trainoffice.controller;

import com.hsf302.trainoffice.entity.User;
import com.hsf302.trainoffice.service.RefundService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TicketRefundController {

    private final RefundService refundService;

    public TicketRefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @PostMapping("/tickets/{ticketId}/refund")
    public String requestRefund(@PathVariable Long ticketId,
                                @RequestParam(value = "reason", required = false) String reason,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = currentUser(session);

        if (user == null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Please log in to request a refund."
            );
            return "redirect:/login";
        }

        try {
            refundService.createRefundRequest(ticketId, user, reason);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Refund request has been sent successfully. Please wait for admin approval."
            );

        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    ex.getMessage()
            );

        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Cannot create refund request. Please try again."
            );
        }

        return "redirect:/tickets";
    }

    private User currentUser(HttpSession session) {
        Object sessionUser = session.getAttribute("currentUser");
        return sessionUser instanceof User user ? user : null;
    }
}