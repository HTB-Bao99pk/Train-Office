package com.hsf302.trainoffice.repository;

import com.hsf302.trainoffice.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainRepository extends JpaRepository<Train, Long> {
    Optional<Train> findByCode(String code);
    boolean existsByCode(String code);
}