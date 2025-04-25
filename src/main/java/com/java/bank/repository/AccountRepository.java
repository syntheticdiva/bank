package com.java.bank.repository;

import com.java.bank.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE " +
            "ROUND(a.balance, 4) < ROUND(a.initialBalance * 2.07, 4) AND " +
            "ROUND(a.balance * 1.10, 4) > ROUND(a.balance, 4) AND " +
            "ROUND(a.balance * 1.10, 4) <= ROUND(a.initialBalance * 2.07, 4)")
    List<Account> findAccountsForInterestApply();
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.user.id = :userId")
    @Cacheable(value = "accounts", key = "'account_by_user:' + #userId", unless = "#result == null")
    Optional<Account> findByUserIdWithLock(@Param("userId") Long userId);

    @Override
    @CacheEvict(value = "accounts", key = "'account_by_user:' + #entity.user.id")
    <S extends Account> S save(S entity);

    @Override
    @CacheEvict(value = "accounts", key = "'account_by_user:' + #entity.user.id")
    void delete(Account entity);
}
