package com.hsf302.trainoffice.controller;

import com.hsf302.trainoffice.dto.CreateBookingRequest;
import com.hsf302.trainoffice.entity.Booking;
import com.hsf302.trainoffice.entity.Payment;
import com.hsf302.trainoffice.entity.User;
import com.hsf302.trainoffice.service.BookingService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/booking")
    public ResponseEntity<?> createBooking(@Valid @RequestBody CreateBookingRequest request,
                                           BindingResult bindingResult,
                                           HttpSession session) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid booking request"));
        }

        Booking booking;
        try {
            Object sessionUser = session.getAttribute("currentUser");
            if (sessionUser instanceof User user) {
                booking = bookingService.createBookingForUser(request, user);
            } else {
                booking = bookingService.createBookingForGuest(request);
            }
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }

        Payment payment = booking.getPayments().isEmpty() ? null : booking.getPayments().get(0);
        if (payment == null) {
            return ResponseEntity.ok(Map.of(
                    "bookingId", booking.getBookingId(),
                    "bookingNumber", booking.getBookingNumber(),
                    "bookingStatus", booking.getBookingStatus()
            ));
        }

        return ResponseEntity.ok(Map.of(
                "bookingId", booking.getBookingId(),
                "bookingNumber", booking.getBookingNumber(),
                "bookingStatus", booking.getBookingStatus(),
                "paymentId", payment.getPaymentId(),
                "paymentStatus", payment.getPaymentStatus()
        ));
    }
}
