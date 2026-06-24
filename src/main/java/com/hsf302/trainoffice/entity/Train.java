package com.hsf302.trainoffice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
// Station.java
// Train.java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Train {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trainId;

    @Column(unique = true, nullable = false)
    private String trainCode;

    private String trainName;
    private String trainType;

    @OneToMany(mappedBy = "train")
    private List<Coach> coaches = new ArrayList<>();

    @OneToMany(mappedBy = "train")
    private List<TrainTrip> trainTrips = new ArrayList<>();
}