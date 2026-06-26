package com.hsf302.trainoffice.service;

import com.hsf302.trainoffice.entity.Route;
import com.hsf302.trainoffice.entity.Station;

import java.util.List;
import java.util.Optional;

public interface RouteService {

    List<Route> getAllRoutes();
    List<Route> getRoutesByStartAndEndStation(Station startStation, Station endStation);
    Route createRoute(Route route);
    Route updateRoute(Integer id, Route route);
    void deleteRoute(Integer id);
    boolean routeExists(String code);
    List<Route> findRouteByStations(Integer startStationId, Integer endStationId);
    List<Route> findByStartStationIdAndEndStationId(Integer startStationId, Integer endStationId);
    Optional<Route> findById(Integer id);
    List<Route> findAllAndFetchStations();

}

