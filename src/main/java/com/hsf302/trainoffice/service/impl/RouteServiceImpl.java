package com.hsf302.trainoffice.service.impl;

import com.hsf302.trainoffice.entity.Route;
import com.hsf302.trainoffice.repository.RouteRepository;
import com.hsf302.trainoffice.service.RouteService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;

    public RouteServiceImpl(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Override
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    @Override
    public Optional<Route> getRouteById(Long id) {
        return routeRepository.findById(id);
    }

    @Override
    public Route saveRoute(Route route) {

        if (route.getRouteId() == null) {

            if (routeRepository.existsByRouteCode(route.getRouteCode())) {
                throw new IllegalStateException("Route code already exists!");
            }

        } else {

            Optional<Route> oldRoute =
                    routeRepository.findById(route.getRouteId());

            if (oldRoute.isPresent()) {

                if (!oldRoute.get().getRouteCode().equals(route.getRouteCode())
                        && routeRepository.existsByRouteCode(route.getRouteCode())) {

                    throw new IllegalStateException("Route code already exists!");
                }
            }
        }

        return routeRepository.save(route);
    }

    @Override
    public void deleteRoute(Long id) {

        if (!routeRepository.existsById(id)) {
            throw new RuntimeException("Route not found");
        }

        routeRepository.deleteById(id);
    }
}