package com.hsf302.trainoffice.common;

import lombok.Getter;

@Getter
public enum RefundStatus {
    PENDING("Chờ duyệt"),
    APPROVED("Đã hoàn tiền"),
    REJECTED("Đã từ chối");

    private final String description;

    RefundStatus(String description) {
        this.description = description;
    }
}
