package com.hsf302.trainoffice.service;

import com.hsf302.trainoffice.entity.Refund;
import com.hsf302.trainoffice.entity.User;

import java.util.List;

public interface RefundService {

    Refund createRefundRequest(Long ticketId, User user, String reason);

    List<Refund> getPendingRefunds();

    void approveRefund(Long refundId, User adminUser);

    void rejectRefund(Long refundId, User adminUser);
}