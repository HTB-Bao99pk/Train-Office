package com.hsf302.trainoffice.repository;

import com.hsf302.trainoffice.entity.Route;
import com.hsf302.trainoffice.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Integer> {
    Optional<Route> findByCode(String code);

    List<Route> findByStartStationIdAndEndStationId(Integer startStation, Integer endStation);

    List<Route> findByStatus(Route.Status status);

    boolean existsByCode(String code);

    List<Route> startStationId(Integer startStation);

    List<Route> findByStartStationAndEndStation(Station startStation, Station endStation);

    @Query("SELECT r FROM Route r JOIN FETCH r.startStation JOIN FETCH r.endStation")
    List<Route> findAllAndFetchStations();
}