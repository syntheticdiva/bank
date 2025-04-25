package com.java.bank.service;

import com.java.bank.entity.Account;
import com.java.bank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterestTransactionalService {
    private final AccountRepository accountRepository;

    @Transactional
    public List<Account> findAccountsForInterestApply() {
        return accountRepository.findAccountsForInterestApply();
    }

    @Transactional
    public void saveAllAccounts(List<Account> accounts) {
        accountRepository.saveAll(accounts);
    }
}
