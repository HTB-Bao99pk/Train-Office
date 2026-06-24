package com.hsf302.trainoffice.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
// TripStation.java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripStation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tripStationId;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private TrainTrip trainTrip;

    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    private Integer stationOrder;
    private LocalDateTime arrivalTime;
    private LocalDateTime departureTime;
}