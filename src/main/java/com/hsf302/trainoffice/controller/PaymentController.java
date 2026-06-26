package com.hsf302.trainoffice.controller;

import com.hsf302.trainoffice.common.enums.PaymentMethod;
import com.hsf302.trainoffice.common.enums.PaymentStatus;
import com.hsf302.trainoffice.entity.Booking;
import com.hsf302.trainoffice.entity.Payment;
import com.hsf302.trainoffice.repository.BookingRepository;
import com.hsf302.trainoffice.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final BookingRepository bookingRepository;

    public PaymentController(PaymentService paymentService, BookingRepository bookingRepository) {
        this.paymentService = paymentService;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping
    public String listPayments(
            Model model,
            @RequestParam(value = "status", required = false) PaymentStatus status,
            @RequestParam(value = "bookingId", required = false) Long bookingId
    ) {
        List<Payment> payments;

        if (bookingId != null) {
            payments = paymentService.getPaymentsByBookingId(bookingId);
        } else if (status != null) {
            payments = paymentService.getPaymentsByStatus(status);
        } else {
            payments = paymentService.getAllPayments();
        }

        model.addAttribute("payments", payments);
        model.addAttribute("paymentStatuses", PaymentStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedBookingId", bookingId);

        return "payments/list";
    }

    private void addCommonAttributes(Model model) {
        model.addAttribute("allBookings", bookingRepository.findAll());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("paymentStatuses", PaymentStatus.values());
    }

    @GetMapping("/new")
    public String showCreateForm(
            Model model,
            @RequestParam(value = "bookingId", required = false) Long bookingId
    ) {
        Payment payment = new Payment();

        if (bookingId != null) {
            bookingRepository.findById(bookingId).ifPresent(payment::setBooking);
        }

        model.addAttribute("payment", payment);
        addCommonAttributes(model);

        return "payments/create";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable("id") Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Optional<Payment> paymentOpt = paymentService.getPaymentById(id);

        if (paymentOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy payment ID: " + id);
            return "redirect:/payments";
        }

        model.addAttribute("payment", paymentOpt.get());
        addCommonAttributes(model);

        return "payments/edit";
    }

    @PostMapping("/save")
    public String savePayment(
            @Valid @ModelAttribute("payment") Payment payment,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            @RequestParam(value = "bookingId", required = false) Long bookingId
    ) {
        if (bookingId != null) {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

            payment.setBooking(booking);
        }

        if (payment.getBooking() != null && payment.getBooking().getBookingId() != null) {
            Booking booking = bookingRepository.findById(payment.getBooking().getBookingId())
                    .orElseThrow(() -> new RuntimeException(
                            "Booking not found with ID: " + payment.getBooking().getBookingId()
                    ));

            payment.setBooking(booking);
        }

        if (result.hasErrors()) {
            addCommonAttributes(model);
            return payment.getPaymentId() == null ? "payments/create" : "payments/edit";
        }

        try {
            paymentService.savePayment(payment);
            redirectAttributes.addFlashAttribute("successMessage", "Payment đã được lưu thành công!");

            return "redirect:/payments";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi lưu payment: " + e.getMessage());
            addCommonAttributes(model);

            return payment.getPaymentId() == null ? "payments/create" : "payments/edit";
        }
    }

    @GetMapping("/detail/{id}")
    public String showPaymentDetail(
            @PathVariable("id") Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Optional<Payment> paymentOpt = paymentService.getPaymentById(id);

        if (paymentOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy payment ID: " + id);
            return "redirect:/payments";
        }

        model.addAttribute("payment", paymentOpt.get());

        return "payments/detail";
    }

    @GetMapping("/success/{id}")
    public String markPaymentSuccess(
            @PathVariable("id") Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            paymentService.markPaymentSuccess(id);
            redirectAttributes.addFlashAttribute("successMessage", "Payment đã chuyển sang SUCCESS.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi cập nhật payment: " + e.getMessage());
        }

        return "redirect:/payments";
    }

    @GetMapping("/failed/{id}")
    public String markPaymentFailed(
            @PathVariable("id") Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            paymentService.markPaymentFailed(id);
            redirectAttributes.addFlashAttribute("successMessage", "Payment đã chuyển sang FAILED.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi cập nhật payment: " + e.getMessage());
        }

        return "redirect:/payments";
    }

    @GetMapping("/delete/{id}")
    public String deletePayment(
            @PathVariable("id") Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            paymentService.deletePayment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Payment ID " + id + " đã được xoá.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xoá payment: " + e.getMessage());
        }

        return "redirect:/payments";
    }
}