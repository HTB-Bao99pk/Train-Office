package com.hsf302.trainoffice.repository;

import com.hsf302.trainoffice.entity.Coach;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoachRepository extends JpaRepository<Coach, Long> {
}