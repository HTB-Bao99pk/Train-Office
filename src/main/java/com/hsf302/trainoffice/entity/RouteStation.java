package com.hsf302.trainoffice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// RouteStation.java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteStation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routeStationId;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    private Integer stationOrder;
}