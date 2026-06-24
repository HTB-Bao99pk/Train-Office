package com.hsf302.trainoffice.entity;
import com.hsf302.trainoffice.common.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
// Booking.java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @Column(unique = true, nullable = false)
    private String bookingCode;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private TrainTrip trainTrip;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "booking")
    private List<Ticket> tickets = new ArrayList<>();

    @OneToMany(mappedBy = "booking")
    private List<Payment> payments = new ArrayList<>();
}