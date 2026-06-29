package com.hsf302.trainoffice.service.impl;

import com.hsf302.trainoffice.common.enums.PaymentStatus;
import com.hsf302.trainoffice.entity.Payment;
import com.hsf302.trainoffice.repository.PaymentRepository;
import com.hsf302.trainoffice.service.PaymentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        if (status == null) {
            return paymentRepository.findAll();
        }

        return paymentRepository.findByPaymentStatus(status);
    }

    @Override
    public List<Payment> getPaymentsByBookingId(Long bookingId) {
        if (bookingId == null) {
            return paymentRepository.findAll();
        }

        return paymentRepository.findByBooking_BookingId(bookingId);
    }

    @Override
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    @Override
    public Payment savePayment(Payment payment) {
        if (payment.getPaymentStatus() == null) {
            payment.setPaymentStatus(PaymentStatus.PENDING);
        }

        if (payment.getTransactionCode() == null || payment.getTransactionCode().isBlank()) {
            payment.setTransactionCode(generateTransactionCode());
        }

        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS && payment.getPaidAt() == null) {
            payment.setPaidAt(LocalDateTime.now());
        }

        return paymentRepository.save(payment);
    }

    @Override
    public Payment markPaymentSuccess(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));

        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    @Override
    public Payment markPaymentFailed(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));

        payment.setPaymentStatus(PaymentStatus.FAILED);

        return paymentRepository.save(payment);
    }

    @Override
    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new RuntimeException("Payment not found with ID: " + id);
        }

        paymentRepository.deleteById(id);
    }

    private String generateTransactionCode() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}