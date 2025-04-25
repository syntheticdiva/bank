package com.java.bank.service;


import com.java.bank.dto.TransferRequest;
import com.java.bank.entity.Account;
import com.java.bank.exception.AccountNotFoundException;
import com.java.bank.exception.InsufficientFundsException;
import com.java.bank.exception.InvalidTransferException;
import com.java.bank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final AccountRepository accountRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void transferFunds(Long fromUserId, TransferRequest request) {
        Account fromAccount = accountRepository.findByUserIdWithLock(fromUserId)
                .orElseThrow(() -> new AccountNotFoundException("Sender account not found"));

        Account toAccount = accountRepository.findByUserIdWithLock(request.toUserId())
                .orElseThrow(() -> new AccountNotFoundException("Receiver account not found"));

        validateTransfer(fromAccount, request, request.amount());

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.amount()));
        toAccount.setBalance(toAccount.getBalance().add(request.amount()));

        accountRepository.saveAll(List.of(fromAccount, toAccount));
    }

    private void validateTransfer(
            Account fromAccount,
            TransferRequest request,
            BigDecimal amount
    ) {
        if (fromAccount.getUser().getId().equals(request.toUserId())) {
            throw new InvalidTransferException("Cannot transfer to yourself");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferException("Transfer amount must be positive");
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds for transfer");
        }

        if (request.toUserId() == null) {
            throw new InvalidTransferException("Recipient ID is required");
        }
    }
}


















//
//
//    @Transactional
//    public void transferMoney(HttpServletRequest request, Long toUserId, BigDecimal amount) {
//        Long fromUserId = getUserIdFromToken(request);
//        validateTransfer(fromUserId, toUserId, amount);
//
//        Account fromAccount = accountRepository.findByUserIdWithLock(fromUserId)
//                .orElseThrow(() -> new EntityNotFoundException("Sender account not found"));
//        Account toAccount = accountRepository.findByUserIdWithLock(toUserId)
//                .orElseThrow(() -> new EntityNotFoundException("Receiver account not found"));
//
//        performTransfer(fromAccount, toAccount, amount);
//    }
//
//    private void performTransfer(Account fromAccount, Account toAccount, BigDecimal amount) {
//        if (fromAccount.getBalance().compareTo(amount) < 0) {
//            throw new InsufficientFundsException("Insufficient funds for transfer");
//        }
//
//        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
//        toAccount.setBalance(toAccount.getBalance().add(amount));
//
//        // Получаем ID пользователей до завершения транзакции
//        Long fromUserId = fromAccount.getUser().getId();
//        Long toUserId = toAccount.getUser().getId();
//
//        accountRepository.saveAll(List.of(fromAccount, toAccount));
//
//        log.info("Transfer completed: from {} to {}, amount {}", fromUserId, toUserId, amount);
//    }
//    private Long getUserIdFromToken(HttpServletRequest request) {
//        String token = jwtUtil.extractToken(request);
//        return jwtUtil.extractUserId(token);
//    }
//
//    private void validateTransfer(Long fromUserId, Long toUserId, BigDecimal amount) {
//        if (fromUserId.equals(toUserId)) {
//            throw new IllegalArgumentException("Cannot transfer to yourself");
//        }
//
//        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
//            throw new IllegalArgumentException("Transfer amount must be positive");
//        }
//
//        if (!userRepository.existsById(toUserId)) {
//            throw new EntityNotFoundException("Receiver user not found");
//        }
//    }
//
//






//    @Transactional
//    public void transfer(Long fromUserId, Long toUserId, BigDecimal amount) {
//        validateTransfer(fromUserId, toUserId, amount);
//
//        Account fromAccount = accountRepository.findByUserIdWithLock(fromUserId)
//                .orElseThrow(() -> new EntityNotFoundException("Sender account not found"));
//
//        Account toAccount = accountRepository.findByUserIdWithLock(toUserId)
//                .orElseThrow(() -> new EntityNotFoundException("Receiver account not found"));
//
//        performTransfer(fromAccount, toAccount, amount);
//    }
//
//    private void validateTransfer(Long fromUserId, Long toUserId, BigDecimal amount) {
//        if (fromUserId.equals(toUserId)) {
//            throw new IllegalArgumentException("Cannot transfer to yourself");
//        }
//        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
//            throw new IllegalArgumentException("Amount must be positive");
//        }
//        if (!userRepository.existsById(toUserId)) {
//            throw new EntityNotFoundException("Receiver user not found");
//        }
//    }
//
//    private void performTransfer(Account from, Account to, BigDecimal amount) {
//        if (from.getBalance().compareTo(amount) < 0) {
//            throw new InsufficientFundsException("Insufficient funds");
//        }
//
//        from.setBalance(from.getBalance().subtract(amount));
//        to.setBalance(to.getBalance().add(amount));
//
//        accountRepository.saveAll(List.of(from, to));
//    }
