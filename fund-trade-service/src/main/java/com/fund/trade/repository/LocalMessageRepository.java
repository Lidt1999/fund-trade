package com.fund.trade.repository;

import com.fund.trade.entity.LocalMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface LocalMessageRepository extends JpaRepository<LocalMessage, Long> {
    List<LocalMessage> findTop100ByStatusInAndNextRetryTimeLessThanEqualOrderByCreatedAtAsc(
            Collection<Integer> statuses, LocalDateTime now);
}
