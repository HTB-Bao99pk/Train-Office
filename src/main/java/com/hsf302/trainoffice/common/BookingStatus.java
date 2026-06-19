package com.hsf302.trainoffice.common;


public enum BookingStatus {
    BOOKED,     // Đã giữ chỗ (chưa trả tiền)
    PAID,       // Đã thanh toán
    COMPLETED,  // Chuyến đi đã hoàn thành
    CANCELLED,  // Đã hủy (trước khi trả tiền)
    PENDING_REFUND, // Đã thanh toán, đang chờ Admin duyệt hoàn tiền
    REFUNDED        // Đã được Admin duyệt hoàn tiền
}
