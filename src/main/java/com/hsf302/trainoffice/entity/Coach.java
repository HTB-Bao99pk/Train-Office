package com.hsf302.trainoffice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
// Coach.java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coach {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coachId;

    @ManyToOne
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    private String coachNumber;
    private String coachType;
    private Integer capacity;

    @OneToMany(mappedBy = "coach")
    private List<Seat> seats = new ArrayList<>();
}