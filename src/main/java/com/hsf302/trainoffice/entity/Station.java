package com.hsf302.trainoffice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
// Station.java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Station {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stationId;

    @Column(unique = true, nullable = false)
    private String stationCode;

    private String stationName;
    private String city;

    @OneToMany(mappedBy = "station")
    private List<RouteStation> routeStations = new ArrayList<>();

    @OneToMany(mappedBy = "station")
    private List<TripStation> tripStations = new ArrayList<>();
}