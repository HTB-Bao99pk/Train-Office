package com.hsf302.trainoffice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class FareBreakdownItem {
    private int passengerIndex;
    private String passengerName;
    private String passengerType;
    private String passengerPolicyName;

    private String coachNumber;
    private String seatNumber;
    private String seatType;

    private BigDecimal originalPrice;
    private BigDecimal passengerDiscountPercent;
    private BigDecimal passengerDiscountAmount;
    private BigDecimal finalPrice;
}