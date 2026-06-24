package com.hsf302.trainoffice.entity;
import com.hsf302.trainoffice.common.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
// Refund.java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refund {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refundId;

    @Column(unique = true, nullable = false)
    private String refundCode;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    private BigDecimal refundAmount;
    private String refundReason;

    @Enumerated(EnumType.STRING)
    private RefundStatus refundStatus;

    private LocalDateTime refundedAt;
}