package com.hsf302.trainoffice.service.impl;

import com.hsf302.trainoffice.entity.Seat;
import com.hsf302.trainoffice.repository.SeatRepository;
import com.hsf302.trainoffice.service.SeatService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;

    public SeatServiceImpl(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Override
    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    @Override
    public Optional<Seat> getSeatById(Long id) {
        return seatRepository.findById(id);
    }

    @Override
    public Seat saveSeat(Seat seat) {
        if (seat.getSeatId() == null) {
            if (seatRepository.existsByCoachAndSeatNumber(seat.getCoach(), seat.getSeatNumber())) {
                throw new IllegalStateException(
                        "Số ghế '" + seat.getSeatNumber()
                                + "' đã tồn tại trên toa '"
                                + seat.getCoach().getCoachNumber() + "'."
                );
            }
        }
        return seatRepository.save(seat);
    }

    @Override
    public void deleteSeat(Long id) {
        if (!seatRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy Ghế: " + id);
        }
        seatRepository.deleteById(id);
    }
}