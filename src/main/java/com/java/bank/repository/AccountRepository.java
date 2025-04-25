package com.java.bank.repository;

import com.java.bank.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUserId(Long userId);

    @Query("SELECT a.balance FROM Account a WHERE a.user.id = :userId")
    Optional<BigDecimal> findBalanceByUserId(@Param("userId") Long userId);

    @Query("SELECT a FROM Account a WHERE a.balance < a.initialBalance * 2.07 AND a.balance * 1.10 > a.initialBalance * 2.07")
    List<Account> findByBalanceReachedLimit();
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE " +
            "ROUND(a.balance, 4) < ROUND(a.initialBalance * 2.07, 4) AND " +
            "ROUND(a.balance * 1.10, 4) > ROUND(a.balance, 4) AND " +
            "ROUND(a.balance * 1.10, 4) <= ROUND(a.initialBalance * 2.07, 4)")
    List<Account> findAccountsForInterestApply();
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.user.id = :userId")
    Optional<Account> findByUserIdWithLock(@Param("userId") Long userId);
}
