package com.mk.rewards.service;

import com.mk.rewards.dto.RewardSummaryResponse;
import com.mk.rewards.exception.CustomerNotFoundException;
import com.mk.rewards.model.Transaction;
import com.mk.rewards.policy.RewardPolicy;
import com.mk.rewards.policy.DefaultRewardPolicy;
import com.mk.rewards.repository.InMemoryTransactionRepository;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RewardService {

    private final InMemoryTransactionRepository repository;
    private final RewardPolicy rewardPolicy;

    private static final Logger log = LoggerFactory.getLogger(RewardService.class);

    public RewardService() {
        this.repository = new InMemoryTransactionRepository();
        this.rewardPolicy = new DefaultRewardPolicy();
    }

    public RewardSummaryResponse calculateRewards(String customerId, LocalDate fromDate, LocalDate toDate) {
        log.info("Calculating rewards for customerId={} from {} to {}", customerId, fromDate, toDate);
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            log.warn("Invalid date range: fromDate {} is after toDate {}", fromDate, toDate);
            throw new IllegalArgumentException("Invalid date range: fromDate cannot be after toDate.");
        }

        List<Transaction> allTransactions = repository.getAllTransactions();
        boolean customerExists = allTransactions.stream()
            .anyMatch(txn -> txn.getCustomerId().equalsIgnoreCase(customerId));

        if (!customerExists) {
            log.warn("Customer ID '{}' not found in transaction records", customerId);
            throw new CustomerNotFoundException("No customer found with ID: " + customerId);
        }

        List<Transaction> filtered = repository.getTransactions(customerId, fromDate, toDate);

        Map<String, Integer> monthlyPoints = new LinkedHashMap<>();
        int totalPoints = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        for (Transaction txn : filtered) {
            int points = rewardPolicy.calculate(txn.getAmount());
            String month = txn.getTransactionDate().format(formatter);
            monthlyPoints.put(month, monthlyPoints.getOrDefault(month, 0) + points);
            totalPoints += points;
        }

        String customerName = filtered.isEmpty() ? "" : filtered.get(0).getCustomerName();
        if (filtered.isEmpty()) {
            log.warn("No transactions found for customerId={} in given date range", customerId);
            throw new CustomerNotFoundException("No transactions found for the given date range for customer ID: " + customerId);
        }

        log.debug("Reward calculation complete. Total points: {}", totalPoints);

        // Auto-adjust fromDate and toDate if they are null
        LocalDate actualFrom = fromDate != null ? fromDate :
                filtered.stream().map(Transaction::getTransactionDate).min(LocalDate::compareTo).orElse(null);

        LocalDate actualTo = toDate != null ? toDate :
                filtered.stream().map(Transaction::getTransactionDate).max(LocalDate::compareTo).orElse(null);

        return new RewardSummaryResponse(
                customerId,
                customerName,
                actualFrom,
                actualTo,
                monthlyPoints,
                totalPoints,
                filtered
        );
    }
}