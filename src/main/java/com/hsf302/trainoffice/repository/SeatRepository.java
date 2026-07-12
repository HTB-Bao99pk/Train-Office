package com.hsf302.trainoffice.repository;

import com.hsf302.trainoffice.entity.Coach;
import com.hsf302.trainoffice.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    boolean existsByCoachAndSeatNumber(Coach coach, String seatNumber);

    boolean existsByCoach_CoachIdAndSeatNumber(Long coachId, String seatNumber);

    List<Seat> findByCoach_CoachIdOrderBySeatNumberAsc(Long coachId);

    @Query("""
            select s
            from Seat s
                 join fetch s.coach c
                 left join fetch s.compartment cp
            where c.train.trainId = :trainId
            order by c.coachNumber asc, cp.compartmentNumber asc, s.seatNumber asc
            """)
    List<Seat> findSeatsByTrainId(@Param("trainId") Long trainId);
}