package com.java.bank.service;

import com.java.bank.entity.Account;
import com.java.bank.exception.InsufficientFundsException;
import com.java.bank.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    @Scheduled(fixedRate = 30_000)
    @Transactional
    public void applyInterest() {
        List<Account> accounts = accountRepository.findAll();

        accounts.forEach(account -> {
            BigDecimal currentBalance = account.getBalance();
            BigDecimal maxAllowed = account.getInitialBalance().multiply(new BigDecimal("2.07"));

            BigDecimal newBalance = currentBalance.multiply(new BigDecimal("1.10"));

            if (newBalance.compareTo(maxAllowed) > 0) {
                newBalance = maxAllowed;
            }

            if (newBalance.compareTo(currentBalance) > 0) {
                account.setBalance(newBalance);
                accountRepository.save(account);
            }
        });
    }

    @Transactional
    public void updateBalance(Long userId, BigDecimal amount, boolean isDebit) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        BigDecimal newBalance = isDebit
                ? account.getBalance().subtract(amount)
                : account.getBalance().add(amount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("Insufficient balance");
        }

        account.setBalance(newBalance);
        accountRepository.save(account);
    }
}
