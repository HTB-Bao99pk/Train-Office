package com.hsf302.trainoffice.service.impl;

import com.hsf302.trainoffice.entity.Route;
import com.hsf302.trainoffice.entity.RouteStation;
import com.hsf302.trainoffice.entity.Station;
import com.hsf302.trainoffice.repository.RouteRepository;
import com.hsf302.trainoffice.repository.RouteStationRepository;
import com.hsf302.trainoffice.repository.StationRepository;
import com.hsf302.trainoffice.service.RouteStationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RouteStationServiceImpl implements RouteStationService {

    private final RouteStationRepository routeStationRepository;
    private final RouteRepository routeRepository;
    private final StationRepository stationRepository;

    public RouteStationServiceImpl(RouteStationRepository routeStationRepository,
                                   RouteRepository routeRepository,
                                   StationRepository stationRepository) {
        this.routeStationRepository = routeStationRepository;
        this.routeRepository = routeRepository;
        this.stationRepository = stationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteStation> getStationsForRoute(Long routeId) {
        return routeStationRepository.findByRoute_RouteIdOrderByStationOrderAsc(routeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RouteStation> getRouteStationById(Long id) {
        return routeStationRepository.findById(id);
    }

    @Override
    @Transactional
    public RouteStation saveRouteStation(Long routeId, RouteStation routeStation) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new IllegalArgumentException("Route does not exist"));

        Station station = resolveStation(routeStation);

        if (routeStation.getStationOrder() == null || routeStation.getStationOrder() < 1) {
            routeStation.setStationOrder(nextStationOrder(routeId));
        }

        validateUnique(routeId, null, station.getStationId(), routeStation.getStationOrder());

        routeStation.setRoute(route);
        routeStation.setStation(station);

        return routeStationRepository.save(routeStation);
    }

    @Override
    @Transactional
    public RouteStation updateRouteStation(Long routeId, Long routeStationId, RouteStation form) {
        RouteStation existing = routeStationRepository.findById(routeStationId)
                .orElseThrow(() -> new IllegalArgumentException("Route station does not exist"));

        if (!existing.getRoute().getRouteId().equals(routeId)) {
            throw new IllegalArgumentException("Route station does not belong to this route");
        }

        Station station = resolveStation(form);

        if (form.getStationOrder() == null || form.getStationOrder() < 1) {
            form.setStationOrder(existing.getStationOrder());
        }

        validateUnique(routeId, routeStationId, station.getStationId(), form.getStationOrder());

        existing.setStation(station);
        existing.setStationOrder(form.getStationOrder());

        return routeStationRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteRouteStation(Long routeId, Long routeStationId) {
        RouteStation existing = routeStationRepository.findById(routeStationId)
                .orElseThrow(() -> new IllegalArgumentException("Route station does not exist"));

        if (!existing.getRoute().getRouteId().equals(routeId)) {
            throw new IllegalArgumentException("Route station does not belong to this route");
        }

        routeStationRepository.delete(existing);
        routeStationRepository.flush();

        compactStationOrders(routeId);
    }

    @Override
    @Transactional
    public void reorderStations(Long routeId, List<Long> routeStationIds) {
        if (routeStationIds == null || routeStationIds.isEmpty()) {
            throw new IllegalArgumentException("Station order list is empty");
        }

        List<RouteStation> currentStations = routeStationRepository
                .findByRoute_RouteIdOrderByStationOrderAsc(routeId);

        if (currentStations.size() != routeStationIds.size()) {
            throw new IllegalArgumentException("Station order list does not match current route stations");
        }

        Map<Long, RouteStation> stationMap = new HashMap<>();

        for (RouteStation routeStation : currentStations) {
            stationMap.put(routeStation.getRouteStationId(), routeStation);
        }

        for (Long routeStationId : routeStationIds) {
            if (!stationMap.containsKey(routeStationId)) {
                throw new IllegalArgumentException("Invalid route station in reorder list");
            }
        }

        /*
         * Important:
         * route_stations has unique constraint: route_id + station_order.
         * So we first move all orders to temporary negative values,
         * then assign final positive orders.
         */
        int tempOrder = -1;

        for (RouteStation routeStation : currentStations) {
            routeStation.setStationOrder(tempOrder--);
        }

        routeStationRepository.saveAllAndFlush(currentStations);

        for (int index = 0; index < routeStationIds.size(); index++) {
            Long routeStationId = routeStationIds.get(index);
            RouteStation routeStation = stationMap.get(routeStationId);
            routeStation.setStationOrder(index + 1);
        }

        routeStationRepository.saveAll(stationMap.values());
    }

    private Station resolveStation(RouteStation routeStation) {
        if (routeStation.getStation() == null || routeStation.getStation().getStationId() == null) {
            throw new IllegalArgumentException("Station is required");
        }

        return stationRepository.findById(routeStation.getStation().getStationId())
                .orElseThrow(() -> new IllegalArgumentException("Station does not exist"));
    }

    private int nextStationOrder(Long routeId) {
        return routeStationRepository.findByRoute_RouteIdOrderByStationOrderAsc(routeId)
                .stream()
                .map(RouteStation::getStationOrder)
                .filter(order -> order != null && order > 0)
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }

    private void compactStationOrders(Long routeId) {
        List<RouteStation> routeStations = routeStationRepository
                .findByRoute_RouteIdOrderByStationOrderAsc(routeId);

        for (int index = 0; index < routeStations.size(); index++) {
            routeStations.get(index).setStationOrder(index + 1);
        }

        routeStationRepository.saveAll(routeStations);
    }

    private void validateUnique(Long routeId, Long currentRouteStationId, Long stationId, Integer stationOrder) {
        List<RouteStation> existingStations = routeStationRepository.findByRoute_RouteIdOrderByStationOrderAsc(routeId);

        for (RouteStation existing : existingStations) {
            if (currentRouteStationId != null && currentRouteStationId.equals(existing.getRouteStationId())) {
                continue;
            }

            if (existing.getStation().getStationId().equals(stationId)) {
                throw new IllegalArgumentException("Station already exists in this route");
            }

            if (stationOrder != null && existing.getStationOrder().equals(stationOrder)) {
                throw new IllegalArgumentException("Station order already exists in this route");
            }
        }
    }
}