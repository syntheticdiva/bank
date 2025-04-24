package com.java.bank.service;



import com.java.bank.entity.Account;
import com.java.bank.exception.InsufficientFundsException;
import com.java.bank.repository.AccountRepository;
import com.java.bank.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public void transfer(Long fromUserId, Long toUserId, BigDecimal amount) {
        validateTransfer(fromUserId, toUserId, amount);

        Account fromAccount = accountRepository.findByUserIdWithLock(fromUserId)
                .orElseThrow(() -> new EntityNotFoundException("Sender account not found"));

        Account toAccount = accountRepository.findByUserIdWithLock(toUserId)
                .orElseThrow(() -> new EntityNotFoundException("Receiver account not found"));

        performTransfer(fromAccount, toAccount, amount);
    }

    private void validateTransfer(Long fromUserId, Long toUserId, BigDecimal amount) {
        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("Cannot transfer to yourself");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (!userRepository.existsById(toUserId)) {
            throw new EntityNotFoundException("Receiver user not found");
        }
    }

    private void performTransfer(Account from, Account to, BigDecimal amount) {
        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        accountRepository.saveAll(List.of(from, to));
    }
}