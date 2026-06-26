package com.hsf302.trainoffice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "coaches",
        uniqueConstraints = @UniqueConstraint(name = "uk_coaches_train_number", columnNames = {"train_id", "coach_number"}),
        indexes = @Index(name = "idx_coaches_train", columnList = "train_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Coach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coach_id")
    @EqualsAndHashCode.Include
    private Long coachId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "train_id", nullable = false)
    @ToString.Exclude
    private Train train;

    @Column(name = "coach_number", nullable = false, length = 20)
    private String coachNumber;

    @Column(name = "coach_type", nullable = false, length = 50)
    private String coachType;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Builder.Default
    @OneToMany(mappedBy = "coach", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Seat> seats = new ArrayList<>();
}
