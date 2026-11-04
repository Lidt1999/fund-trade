package com.fund.trade.repository;

import com.fund.trade.entity.TaConfirmRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaConfirmRecordRepository extends JpaRepository<TaConfirmRecord, Long> {
    List<TaConfirmRecord> findByConfirmDate(LocalDate confirmDate);
    Optional<TaConfirmRecord> findByOrderId(String orderId);
}
