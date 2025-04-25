package com.java.bank.service;

import com.java.bank.dto.TransferRequest;
import com.java.bank.entity.Account;
import com.java.bank.entity.User;
import com.java.bank.exception.*;
import com.java.bank.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransferService transferService;

    @Test
    void transferFunds_shouldTransferSuccessfully() {
        Long fromUserId = 1L;
        Long toUserId = 2L;
        BigDecimal amount = new BigDecimal("100.00");

        User fromUser = new User();
        fromUser.setId(fromUserId);

        User toUser = new User();
        toUser.setId(toUserId);

        Account fromAccount = new Account();
        fromAccount.setBalance(new BigDecimal("500.00"));
        fromAccount.setUser(fromUser);

        Account toAccount = new Account();
        toAccount.setBalance(new BigDecimal("200.00"));
        toAccount.setUser(toUser);

        when(accountRepository.findByUserIdWithLock(fromUserId))
                .thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByUserIdWithLock(toUserId))
                .thenReturn(Optional.of(toAccount));

        TransferRequest request = new TransferRequest(toUserId, amount);

        transferService.transferFunds(fromUserId, request);

        assertEquals(new BigDecimal("400.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("300.00"), toAccount.getBalance());
        verify(accountRepository).saveAll(List.of(fromAccount, toAccount));
    }

    @Test
    void transferFunds_shouldThrowWhenSenderNotFound() {
        Long fromUserId = 1L;
        TransferRequest request = new TransferRequest(2L, new BigDecimal("100.00"));

        when(accountRepository.findByUserIdWithLock(fromUserId))
                .thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> transferService.transferFunds(fromUserId, request));
    }

    @Test
    void transferFunds_shouldThrowWhenReceiverNotFound() {
        Long fromUserId = 1L;
        Long toUserId = 2L;
        Account fromAccount = new Account();
        fromAccount.setUser(new User());

        when(accountRepository.findByUserIdWithLock(fromUserId))
                .thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByUserIdWithLock(toUserId))
                .thenReturn(Optional.empty());

        TransferRequest request = new TransferRequest(toUserId, new BigDecimal("100.00"));

        assertThrows(AccountNotFoundException.class,
                () -> transferService.transferFunds(fromUserId, request));
    }

    @Test
    void transferFunds_shouldThrowWhenTransferToSelf() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Account fromAccount = new Account();
        fromAccount.setUser(user);
        fromAccount.setBalance(new BigDecimal("500.00"));

        when(accountRepository.findByUserIdWithLock(userId))
                .thenReturn(Optional.of(fromAccount));

        TransferRequest request = new TransferRequest(userId, new BigDecimal("100.00"));

        assertThrows(InvalidTransferException.class,
                () -> transferService.transferFunds(userId, request));
    }
    @Test
    void transferFunds_shouldThrowWhenAmountNotPositive() {
        Long fromUserId = 1L;
        Long toUserId = 2L;
        User fromUser = new User();
        fromUser.setId(fromUserId);

        Account fromAccount = new Account();
        fromAccount.setUser(fromUser);
        fromAccount.setBalance(new BigDecimal("500.00"));

        User toUser = new User();
        toUser.setId(toUserId);
        Account toAccount = new Account();
        toAccount.setUser(toUser);

        when(accountRepository.findByUserIdWithLock(fromUserId))
                .thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByUserIdWithLock(toUserId))
                .thenReturn(Optional.of(toAccount));

        TransferRequest request = new TransferRequest(toUserId, BigDecimal.ZERO);

        Exception exception = assertThrows(InvalidTransferException.class,
                () -> transferService.transferFunds(fromUserId, request));

        assertEquals("Transfer amount must be positive", exception.getMessage());
    }

    @Test
    void transferFunds_shouldThrowWhenInsufficientFunds() {
        Long fromUserId = 1L;
        Long toUserId = 2L;
        User fromUser = new User();
        fromUser.setId(fromUserId);

        Account fromAccount = new Account();
        fromAccount.setUser(fromUser);
        fromAccount.setBalance(new BigDecimal("50.00"));

        User toUser = new User();
        toUser.setId(toUserId);
        Account toAccount = new Account();
        toAccount.setUser(toUser);

        when(accountRepository.findByUserIdWithLock(fromUserId))
                .thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByUserIdWithLock(toUserId))
                .thenReturn(Optional.of(toAccount));

        TransferRequest request = new TransferRequest(toUserId, new BigDecimal("100.00"));

        Exception exception = assertThrows(InsufficientFundsException.class,
                () -> transferService.transferFunds(fromUserId, request));

        assertEquals("Insufficient funds for transfer", exception.getMessage());
    }

}