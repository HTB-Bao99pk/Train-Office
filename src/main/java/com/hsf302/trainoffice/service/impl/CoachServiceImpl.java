package com.hsf302.trainoffice.service.impl;

import com.hsf302.trainoffice.entity.Coach;
import com.hsf302.trainoffice.entity.Seat;
import com.hsf302.trainoffice.repository.CoachRepository;
import com.hsf302.trainoffice.repository.SeatRepository;
import com.hsf302.trainoffice.service.CoachService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CoachServiceImpl implements CoachService {

    private final CoachRepository coachRepository;
    private final SeatRepository seatRepository;

    public CoachServiceImpl(CoachRepository coachRepository,
                            SeatRepository seatRepository) {
        this.coachRepository = coachRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public List<Coach> getAllCoaches() {
        return coachRepository.findAll();
    }

    @Override
    public Optional<Coach> getCoachById(Long id) {
        return coachRepository.findById(id);
    }

    @Override
    @Transactional
    public Coach saveCoach(Coach coach) {
        validateCoach(coach);

        Long trainId = coach.getTrain().getTrainId();
        String coachNumber = normalize(coach.getCoachNumber());

        coach.setCoachNumber(coachNumber);

        if (coach.getCoachId() == null) {
            if (coachRepository.existsByTrain_TrainIdAndCoachNumber(trainId, coachNumber)) {
                throw new IllegalStateException("Coach number already exists in this train.");
            }
        } else {
            Optional<Coach> existingOpt = coachRepository.findById(coach.getCoachId());

            if (existingOpt.isPresent()) {
                Coach existing = existingOpt.get();

                boolean changedTrain = !existing.getTrain().getTrainId().equals(trainId);
                boolean changedNumber = !existing.getCoachNumber().equalsIgnoreCase(coachNumber);

                if ((changedTrain || changedNumber)
                        && coachRepository.existsByTrain_TrainIdAndCoachNumber(trainId, coachNumber)) {
                    throw new IllegalStateException("Coach number already exists in this train.");
                }
            }
        }

        Coach savedCoach = coachRepository.save(coach);

        generateMissingSeats(savedCoach);

        return savedCoach;
    }

    @Override
    public void deleteCoach(Long id) {
        if (!coachRepository.existsById(id)) {
            throw new RuntimeException("Coach not found with ID: " + id);
        }

        coachRepository.deleteById(id);
    }

    private void validateCoach(Coach coach) {
        if (coach.getTrain() == null || coach.getTrain().getTrainId() == null) {
            throw new IllegalStateException("Train is required for coach.");
        }

        if (coach.getCoachNumber() == null || coach.getCoachNumber().isBlank()) {
            throw new IllegalStateException("Coach number is required.");
        }

        if (coach.getCoachType() == null || coach.getCoachType().isBlank()) {
            throw new IllegalStateException("Coach type is required.");
        }

        if (coach.getCapacity() == null || coach.getCapacity() <= 0) {
            throw new IllegalStateException("Coach capacity must be greater than 0.");
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }

    private void generateMissingSeats(Coach coach) {
        int capacity = coach.getCapacity() == null ? 0 : coach.getCapacity();

        if (capacity <= 0) {
            return;
        }

        String seatType = coach.getCoachType();
        BigDecimal extraPrice = resolveExtraPrice(seatType);

        for (int index = 1; index <= capacity; index++) {
            String seatNumber = generateSeatNumber(index);

            boolean exists = seatRepository.existsByCoach_CoachIdAndSeatNumber(
                    coach.getCoachId(),
                    seatNumber
            );

            if (exists) {
                continue;
            }

            Seat seat = Seat.builder()
                    .coach(coach)
                    .seatNumber(seatNumber)
                    .seatType(seatType)
                    .extraPrice(extraPrice)
                    .build();

            seatRepository.save(seat);
        }
    }

    private String generateSeatNumber(int index) {
        String[] columns = {"A", "B", "C", "D"};

        int row = ((index - 1) / columns.length) + 1;
        String column = columns[(index - 1) % columns.length];

        return String.format("%02d%s", row, column);
    }

    private BigDecimal resolveExtraPrice(String coachType) {
        if (coachType == null) {
            return BigDecimal.ZERO;
        }

        String type = coachType.toLowerCase();

        if (type.contains("vip")) {
            return new BigDecimal("180000");
        }

        if (type.contains("first")) {
            return new BigDecimal("90000");
        }

        if (type.contains("soft sleeper")) {
            return new BigDecimal("65000");
        }

        if (type.contains("soft seat")) {
            return new BigDecimal("30000");
        }

        return BigDecimal.ZERO;
    }
}