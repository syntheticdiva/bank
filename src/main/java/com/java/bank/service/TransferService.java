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

