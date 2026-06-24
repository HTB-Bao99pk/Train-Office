package com.hsf302.trainoffice.entity;
import com.hsf302.trainoffice.common.enums.TripStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
// TrainTrip.java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainTrip {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tripId;

    @ManyToOne
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal basePrice;

    @Enumerated(EnumType.STRING)
    private TripStatus status;

    @OneToMany(mappedBy = "trainTrip")
    private List<TripStation> tripStations = new ArrayList<>();

    @OneToMany(mappedBy = "trainTrip")
    private List<Booking> bookings = new ArrayList<>();
}
