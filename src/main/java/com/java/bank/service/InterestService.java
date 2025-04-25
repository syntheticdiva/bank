package com.java.bank.service;

import com.java.bank.entity.Account;
import com.java.bank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ScheduledFuture;


@Service
@RequiredArgsConstructor
@Slf4j
public class InterestService implements SchedulingConfigurer {
    private final TaskScheduler taskScheduler;

    private ScheduledFuture<?> scheduledTask;
    private volatile boolean shouldContinueScheduling = true;
    private final InterestTransactionalService transactionalService;

    private static final BigDecimal RATE = new BigDecimal("1.10");
    private static final BigDecimal MAX_MULTIPLIER = new BigDecimal("2.07");
    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final BigDecimal EPSILON = new BigDecimal("0.0001");

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        scheduledTask = taskScheduler.scheduleAtFixedRate(
                this::applyInterestWithTransaction,
                30_000
        );
    }
    public void applyInterestWithTransaction() {
        try {
            applyInterestToAllAccounts();
        } catch (Exception e) {
            log.error("Error in scheduled task: {}", e.getMessage(), e);
        }
    }

    public void applyInterestToAllAccounts() {
        if (!shouldContinueScheduling) {
            log.debug("Interest application is already disabled");
            return;
        }

        log.info("Starting interest calculation");

        List<Account> accounts = transactionalService.findAccountsForInterestApply();
        log.info("Found {} accounts for processing", accounts.size());

        if (accounts.isEmpty()) {
            log.info("All accounts have reached the limit. Disabling further interest calculations.");
            shouldContinueScheduling = false;
            if (scheduledTask != null) {
                scheduledTask.cancel(false);
            }
            return;
        }

        List<Account> updatedAccounts = new ArrayList<>();
        boolean anyAccountUpdated = false;

        for (Account account : accounts) {
            try {
                BigDecimal originalBalance = account.getBalance().setScale(SCALE, ROUNDING_MODE);
                BigDecimal maxAllowed = calculateMaxAllowed(account).setScale(SCALE, ROUNDING_MODE);

                log.debug("Processing account {}: current={}, max={}",
                        account.getId(), originalBalance, maxAllowed);

                if (originalBalance.compareTo(maxAllowed) >= 0) {
                    log.debug("Account {} already at max limit", account.getId());
                    continue;
                }

                BigDecimal newBalance = originalBalance.multiply(RATE)
                        .setScale(SCALE, ROUNDING_MODE)
                        .min(maxAllowed);

                BigDecimal difference = newBalance.subtract(originalBalance);

                if (difference.compareTo(EPSILON) > 0) {
                    account.setBalance(newBalance);
                    updatedAccounts.add(account);
                    anyAccountUpdated = true;
                    log.info("Account {} updated: {} → {} (+{})",
                            account.getId(),
                            format(originalBalance),
                            format(newBalance),
                            format(difference));
                } else {
                    log.warn("Account {} skipped - no meaningful increase ({} < ε)",
                            account.getId(), difference);
                }
            } catch (Exception ex) {
                log.error("Error processing account {}: {}", account.getId(), ex.getMessage(), ex);
            }
        }

        if (!anyAccountUpdated && !accounts.isEmpty()) {
            log.warn("No accounts were updated despite having {} candidates. Possible rounding issue.",
                    accounts.size());
            shouldContinueScheduling = false;
            if (scheduledTask != null) {
                scheduledTask.cancel(false);
            }
        }

        if (!updatedAccounts.isEmpty()) {
            try {
                transactionalService.saveAllAccounts(updatedAccounts);
                log.info("Successfully updated {} accounts", updatedAccounts.size());
            } catch (DataIntegrityViolationException e) {
                log.error("Constraint violation while saving: {}", e.getMessage());
            }
        }
    }

    private BigDecimal calculateMaxAllowed(Account account) {
        return account.getInitialBalance()
                .multiply(MAX_MULTIPLIER)
                .setScale(SCALE, ROUNDING_MODE);
    }

    private String format(BigDecimal amount) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(amount);
    }
}

