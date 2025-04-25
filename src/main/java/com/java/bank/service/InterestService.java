package com.java.bank.service;

import com.java.bank.entity.Account;
import com.java.bank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterestService {
    private final AccountRepository accountRepository;

    @Scheduled(fixedRate = 30_000)
    @Transactional
    public void applyInterestToAllAccounts() {
        log.info("Начало начисления процентов по счетам");

        List<Account> accounts = accountRepository.findAccountsForInterestApply();
        log.debug("Найдено {} счетов для начисления процентов", accounts.size());

        BigDecimal totalInterest = BigDecimal.ZERO;
        int updatedAccounts = 0;

        for (Account account : accounts) {
            try {
                BigDecimal originalBalance = account.getBalance();
                BigDecimal newBalance = calculateNewBalance(account);
                BigDecimal interest = newBalance.subtract(originalBalance);

                account.setBalance(newBalance);
                totalInterest = totalInterest.add(interest);
                updatedAccounts++;

                log.debug("Счёт #{}: {} -> {} (+{})",
                        account.getId(),
                        formatMoney(originalBalance),
                        formatMoney(newBalance),
                        formatMoney(interest));

            } catch (Exception ex) {
                log.error("Ошибка при обработке счёта #{}: {}", account.getId(), ex.getMessage());
            }
        }

        accountRepository.saveAll(accounts);

        log.info("Начисление завершено. Обновлено счетов: {}. Общая сумма начислений: {}",
                updatedAccounts,
                formatMoney(totalInterest));
    }

    private BigDecimal calculateNewBalance(Account account) {
        BigDecimal maxAllowed = account.getInitialBalance().multiply(BigDecimal.valueOf(2.07));
        BigDecimal increasedBalance = account.getBalance().multiply(BigDecimal.valueOf(1.10));
        return increasedBalance.min(maxAllowed);
    }

    private String formatMoney(BigDecimal amount) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(amount);
    }
}
