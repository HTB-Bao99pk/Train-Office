package com.hsf302.trainoffice.entity;
import com.hsf302.trainoffice.common.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
// Ticket.java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @Column(unique = true, nullable = false)
    private String ticketCode;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    private BigDecimal ticketPrice;

    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus;

    @OneToMany(mappedBy = "ticket")
    private List<Refund> refunds = new ArrayList<>();
}