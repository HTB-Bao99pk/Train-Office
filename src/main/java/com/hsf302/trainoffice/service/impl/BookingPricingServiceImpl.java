package com.hsf302.trainoffice.service.impl;

import com.hsf302.trainoffice.entity.Seat;
import com.hsf302.trainoffice.entity.TrainTrip;
import com.hsf302.trainoffice.service.BookingPricingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BookingPricingServiceImpl implements BookingPricingService {
    @Override
    public BigDecimal ticketPrice(TrainTrip trainTrip, Seat seat) {
        BigDecimal basePrice = trainTrip.getBasePrice() == null ? BigDecimal.ZERO : trainTrip.getBasePrice();
        BigDecimal extraPrice = seat.getExtraPrice() == null ? BigDecimal.ZERO : seat.getExtraPrice();
        return basePrice.add(extraPrice);
    }

    @Override
    public BigDecimal calculateTotal(TrainTrip trainTrip, List<Seat> seats) {
        BigDecimal total = BigDecimal.ZERO;
        for (Seat seat : seats) {
            total = total.add(ticketPrice(trainTrip, seat));
        }
        return total;
    }
}
