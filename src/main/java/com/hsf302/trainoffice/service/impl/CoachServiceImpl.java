package com.hsf302.trainoffice.service.impl;

import com.hsf302.trainoffice.entity.Coach;
import com.hsf302.trainoffice.repository.CoachRepository;
import com.hsf302.trainoffice.service.CoachService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CoachServiceImpl implements CoachService {

    private final CoachRepository coachRepository;

    public CoachServiceImpl(CoachRepository coachRepository) {
        this.coachRepository = coachRepository;
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
    public Coach saveCoach(Coach coach) {
        if (coach.getTrain() == null || coach.getTrain().getTrainId() == null) {
            throw new IllegalStateException("Train is required for coach");
        }

        Long trainId = coach.getTrain().getTrainId();
        String coachNumber = coach.getCoachNumber();

        if (coachNumber == null || coachNumber.isBlank()) {
            throw new IllegalStateException("Coach number is required");
        }

        if (coach.getCoachId() == null) {
            if (coachRepository.existsByTrain_TrainIdAndCoachNumber(trainId, coachNumber)) {
                throw new IllegalStateException("Coach number already exists in this train");
            }
        } else {
            Optional<Coach> existing = coachRepository.findById(coach.getCoachId());
            if (existing.isPresent()) {
                Coach oldCoach = existing.get();
                boolean changedTrain = !oldCoach.getTrain().getTrainId().equals(trainId);
                boolean changedNumber = !oldCoach.getCoachNumber().equals(coachNumber);
                if ((changedTrain || changedNumber)
                        && coachRepository.existsByTrain_TrainIdAndCoachNumber(trainId, coachNumber)) {
                    throw new IllegalStateException("Coach number already exists in this train");
                }
            }
        }
        return coachRepository.save(coach);
    }

    @Override
    public void deleteCoach(Long id) {
        if (!coachRepository.existsById(id)) {
            throw new RuntimeException("Coach not found with ID: " + id);
        }
        coachRepository.deleteById(id);
    }
}
