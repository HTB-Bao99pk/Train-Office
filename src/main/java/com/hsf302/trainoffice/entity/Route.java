package com.hsf302.trainoffice.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
// Route.java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routeId;

    @Column(unique = true, nullable = false)
    private String routeCode;

    private String routeName;
    private Double distanceKm;

    @OneToMany(mappedBy = "route")
    private List<RouteStation> routeStations = new ArrayList<>();

    @OneToMany(mappedBy = "route")
    private List<TrainTrip> trainTrips = new ArrayList<>();
}