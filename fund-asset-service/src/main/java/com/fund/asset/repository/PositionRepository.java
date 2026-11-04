package com.fund.asset.repository;

import com.fund.asset.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Long> {
    Optional<Position> findByUserIdAndProductCode(Long userId, String productCode);

    @Modifying
    @Query("update Position p set p.shares = p.shares - :shares, p.frozenShares = p.frozenShares + :shares "
            + "where p.userId = :userId and p.productCode = :productCode and p.shares >= :shares")
    int freezeShares(@Param("userId") Long userId, @Param("productCode") String productCode,
                     @Param("shares") Long shares);
}
