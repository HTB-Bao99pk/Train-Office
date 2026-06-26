package com.hsf302.trainoffice.service;

import com.hsf302.trainoffice.entity.Coach;

import java.util.List;
import java.util.Optional;

public interface CoachService {
    List<Coach> getAllCoaches();
    Optional<Coach> getCoachById(Long id);
    Coach saveCoach(Coach coach);
    void deleteCoach(Long id);
}