package com.hsf302.trainoffice.repository;

import com.hsf302.trainoffice.entity.Carriage;
import com.hsf302.trainoffice.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    boolean existsByCarriageAndSeatNumber(Carriage carriage, String seatNumber);
}