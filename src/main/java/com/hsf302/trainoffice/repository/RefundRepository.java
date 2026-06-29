package com.hsf302.trainoffice.repository;

import com.hsf302.trainoffice.common.enums.RefundStatus;
import com.hsf302.trainoffice.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
    long countByRefundStatus(RefundStatus refundStatus);
}
