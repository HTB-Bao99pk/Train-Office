package com.hsf302.trainoffice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
// Seat.java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;

    @ManyToOne
    @JoinColumn(name = "coach_id", nullable = false)
    private Coach coach;

    private String seatNumber;
    private String seatType;
    private BigDecimal extraPrice;

    @OneToMany(mappedBy = "seat")
    private List<Ticket> tickets = new ArrayList<>();
}