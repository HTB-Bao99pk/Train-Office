package com.hsf302.trainoffice.service.impl;

import com.hsf302.trainoffice.dto.PassengerBookingRequest;
import com.hsf302.trainoffice.entity.DiscountPolicy;
import com.hsf302.trainoffice.entity.Seat;
import com.hsf302.trainoffice.entity.TrainTrip;
import com.hsf302.trainoffice.service.BookingPricingService;
import com.hsf302.trainoffice.service.DiscountPolicyService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class BookingPricingServiceImpl implements BookingPricingService {

    private final DiscountPolicyService discountPolicyService;

    public BookingPricingServiceImpl(DiscountPolicyService discountPolicyService) {
        this.discountPolicyService = discountPolicyService;
    }

    @Override
    public BigDecimal ticketPrice(TrainTrip trainTrip, Seat seat) {
        return ticketPrice(trainTrip, seat, "ADULT");
    }

    @Override
    public BigDecimal ticketPrice(TrainTrip trainTrip, Seat seat, String passengerType) {
        BigDecimal originalPrice = originalTicketPrice(trainTrip, seat);

        DiscountPolicy policy = discountPolicyService
                .getActivePolicyByCode(passengerType)
                .orElse(null);

        if (policy == null || policy.getDiscountPercent() == null) {
            return originalPrice.setScale(0, RoundingMode.HALF_UP);
        }

        BigDecimal discountPercent = policy.getDiscountPercent();

        if (discountPercent.compareTo(BigDecimal.valueOf(100)) >= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountAmount = originalPrice
                .multiply(discountPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal finalPrice = originalPrice.subtract(discountAmount);

        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
        }

        return finalPrice.setScale(0, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateTotal(TrainTrip trainTrip, List<Seat> seats) {
        BigDecimal total = BigDecimal.ZERO;

        for (Seat seat : seats) {
            total = total.add(ticketPrice(trainTrip, seat));
        }

        return total;
    }

    @Override
    public BigDecimal calculateTotal(TrainTrip trainTrip,
                                     List<Seat> seats,
                                     List<PassengerBookingRequest> passengers) {
        BigDecimal total = BigDecimal.ZERO;

        for (int i = 0; i < seats.size(); i++) {
            String passengerType = "ADULT";

            if (passengers != null
                    && i < passengers.size()
                    && passengers.get(i).getPassengerType() != null) {
                passengerType = passengers.get(i).getPassengerType();
            }

            total = total.add(ticketPrice(trainTrip, seats.get(i), passengerType));
        }

        return total;
    }

    private BigDecimal originalTicketPrice(TrainTrip trainTrip, Seat seat) {
        BigDecimal basePrice = trainTrip.getBasePrice() == null
                ? BigDecimal.ZERO
                : trainTrip.getBasePrice();

        BigDecimal extraPrice = seat.getExtraPrice() == null
                ? BigDecimal.ZERO
                : seat.getExtraPrice();

        return basePrice.add(extraPrice);
    }
}