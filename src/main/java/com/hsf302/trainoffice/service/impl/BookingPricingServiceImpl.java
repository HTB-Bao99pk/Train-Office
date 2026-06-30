package com.hsf302.trainoffice.service.impl;

import com.hsf302.trainoffice.dto.PassengerBookingRequest;
import com.hsf302.trainoffice.entity.Seat;
import com.hsf302.trainoffice.entity.TrainTrip;
import com.hsf302.trainoffice.service.BookingPricingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class BookingPricingServiceImpl implements BookingPricingService {

    @Override
    public BigDecimal ticketPrice(TrainTrip trainTrip, Seat seat) {
        return ticketPrice(trainTrip, seat, "ADULT");
    }

    @Override
    public BigDecimal ticketPrice(TrainTrip trainTrip, Seat seat, String passengerType) {
        BigDecimal basePrice = trainTrip.getBasePrice() == null
                ? BigDecimal.ZERO
                : trainTrip.getBasePrice();

        BigDecimal extraPrice = seat.getExtraPrice() == null
                ? BigDecimal.ZERO
                : seat.getExtraPrice();

        BigDecimal originalPrice = basePrice.add(extraPrice);
        BigDecimal finalPrice = applyPassengerDiscount(originalPrice, passengerType);

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

    private BigDecimal applyPassengerDiscount(BigDecimal originalPrice, String passengerType) {
        if ("INFANT".equalsIgnoreCase(passengerType)) {
            return BigDecimal.ZERO;
        }

        if ("CHILD".equalsIgnoreCase(passengerType)) {
            return originalPrice.multiply(BigDecimal.valueOf(0.5));
        }

        if ("SENIOR".equalsIgnoreCase(passengerType)) {
            return originalPrice.multiply(BigDecimal.valueOf(0.75));
        }

        return originalPrice;
    }
}