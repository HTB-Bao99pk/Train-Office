package com.hsf302.trainoffice.service;

import com.hsf302.trainoffice.entity.Seat;
import com.hsf302.trainoffice.entity.Ticket;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TicketService {
    boolean isSeatAvailable(Long seatId, Long tripId, Integer departureOrder, Integer arrivalOrder);

    Set<Long> findBlockedSeatIds(Long tripId, Integer departureOrder, Integer arrivalOrder);

    List<Seat> getSeatsForTrip(Long tripId);

    List<Ticket> getTicketsByBookingId(Long bookingId);

    Optional<Ticket> getTicketById(Long ticketId);
}
