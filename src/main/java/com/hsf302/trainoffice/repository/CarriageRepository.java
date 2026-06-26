package com.hsf302.trainoffice.repository;

import com.hsf302.trainoffice.entity.Carriage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarriageRepository extends JpaRepository<Carriage, Long> {
}