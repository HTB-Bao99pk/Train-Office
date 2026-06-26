package com.hsf302.trainoffice.service.impl;

import com.hsf302.trainoffice.entity.Train;
import com.hsf302.trainoffice.repository.TrainRepository;
import com.hsf302.trainoffice.service.TrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainServiceImpl implements TrainService {

    private final TrainRepository trainRepository;

    @Autowired
    public TrainServiceImpl(TrainRepository trainRepository) {
        this.trainRepository = trainRepository;
    }

    @Override
    public List<Train> getAllTrains() {
        return trainRepository.findAll();
    }

    @Override
    public Optional<Train> getTrainById(Long id) {
        return trainRepository.findById(id);
    }

    @Override
    public Train saveTrain(Train train) {

        if (train.getTrainId() == null) {

            if (trainRepository.existsByTrainCode(train.getTrainCode())) {
                throw new IllegalStateException(
                        "Train code '" + train.getTrainCode() + "' already exists!"
                );
            }

        }

        return trainRepository.save(train);
    }

    @Override
    public void deleteTrain(Long id) {
        if (!trainRepository.existsById(id)) {
            throw new RuntimeException("Train not found with ID: " + id);
        }
        trainRepository.deleteById(id);
    }
}

