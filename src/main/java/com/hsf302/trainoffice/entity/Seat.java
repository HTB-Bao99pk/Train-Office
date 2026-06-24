package com.hsf302.trainoffice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "seats",
        uniqueConstraints = @UniqueConstraint(name = "uk_seats_coach_number", columnNames = {"coach_id", "seat_number"}),
        indexes = @Index(name = "idx_seats_coach", columnList = "coach_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    @EqualsAndHashCode.Include
    private Long seatId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coach_id", nullable = false)
    @ToString.Exclude
    private Coach coach;

    @Column(name = "seat_number", nullable = false, length = 20)
    private String seatNumber;

    @Column(name = "seat_type", nullable = false, length = 50)
    private String seatType;

    @Column(name = "extra_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal extraPrice;

    @Builder.Default
    @OneToMany(mappedBy = "seat", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Ticket> tickets = new ArrayList<>();
}
