package com.hsf302.trainoffice.repository;

import com.hsf302.trainoffice.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {

    boolean existsByRouteCode(String routeCode);

}