package com.hsf302.trainoffice.service;

import com.hsf302.trainoffice.entity.Seat;
import com.hsf302.trainoffice.entity.TrainTrip;

import java.math.BigDecimal;
import java.util.List;

public interface BookingPricingService {
    BigDecimal ticketPrice(TrainTrip trainTrip, Seat seat);

    BigDecimal calculateTotal(TrainTrip trainTrip, List<Seat> seats);
}
