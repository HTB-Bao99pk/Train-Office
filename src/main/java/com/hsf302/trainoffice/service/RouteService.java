package com.hsf302.trainoffice.service;

import com.hsf302.trainoffice.entity.Route;

import java.util.List;
import java.util.Optional;

public interface RouteService {

    List<Route> getAllRoutes();

    Optional<Route> getRouteById(Long id);

    Route saveRoute(Route route);

    void deleteRoute(Long id);
}