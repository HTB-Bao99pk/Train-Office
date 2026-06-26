package com.hsf302.trainoffice.repository;

import com.hsf302.trainoffice.entity.Coach;
import com.hsf302.trainoffice.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    boolean existsByCoachAndSeatNumber(Coach coach, String seatNumber);
}