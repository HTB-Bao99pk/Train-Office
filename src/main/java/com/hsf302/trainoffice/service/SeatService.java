package com.hsf302.trainoffice.service;

import com.hsf302.trainoffice.entity.Seat;

import java.util.List;
import java.util.Optional;

public interface SeatService {
    List<Seat> getAllSeats();
    Optional<Seat> getSeatById(Long id);
    Seat saveSeat(Seat seat);
    void deleteSeat(Long id);
}
