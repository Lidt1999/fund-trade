package com.fund.payment.repository;

import com.fund.payment.entity.PaymentAccount;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface PaymentAccountRepository extends JpaRepository<PaymentAccount, Long> {
    Optional<PaymentAccount> findByUserId(Long userId);
    @Modifying
    @Query("update PaymentAccount a set a.availableBalance = a.availableBalance - :amount, a.frozenBalance = a.frozenBalance + :amount where a.userId = :userId and a.availableBalance >= :amount")
    int freeze(@Param("userId") Long userId, @Param("amount") Long amount);
    @Modifying
    @Query("update PaymentAccount a set a.frozenBalance = a.frozenBalance - :amount where a.userId = :userId and a.frozenBalance >= :amount")
    int debit(@Param("userId") Long userId, @Param("amount") Long amount);
    @Modifying
    @Query("update PaymentAccount a set a.availableBalance = a.availableBalance + :amount, a.frozenBalance = a.frozenBalance - :amount where a.userId = :userId and a.frozenBalance >= :amount")
    int release(@Param("userId") Long userId, @Param("amount") Long amount);
    @Modifying
    @Query("update PaymentAccount a set a.availableBalance = a.availableBalance + :amount where a.userId = :userId")
    int credit(@Param("userId") Long userId, @Param("amount") Long amount);
}
