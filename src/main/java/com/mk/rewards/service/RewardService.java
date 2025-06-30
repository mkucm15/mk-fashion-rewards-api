package com.mk.rewards.service;

import com.mk.rewards.dto.RewardSummaryResponse;
import com.mk.rewards.exception.CustomerNotFoundException;
import com.mk.rewards.model.Transaction;
import com.mk.rewards.policy.RewardPolicy;
import com.mk.rewards.repository.InMemoryTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public RewardService(InMemoryTransactionRepository repository, RewardPolicy rewardPolicy) {
        this.repository = repository;
        this.rewardPolicy = rewardPolicy;
    }

    public RewardSummaryResponse calculateRewards(String customerId, LocalDate fromDate, LocalDate toDate) {
        log.info("Calculating rewards for customerId={} from {} to {}", customerId, fromDate, toDate);
        List<Transaction> allTransactions = repository.getAllTransactions();

        List<Transaction> filtered = new ArrayList<>();
        for (Transaction txn : allTransactions) {
            if (txn.getCustomerId().equalsIgnoreCase(customerId)) {
                if ((fromDate == null || !txn.getTransactionDate().isBefore(fromDate)) &&
                        (toDate == null || !txn.getTransactionDate().isAfter(toDate))) {
                    filtered.add(txn);
                }
            }
        }

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
            throw new CustomerNotFoundException("No transactions found for customer ID: " + customerId);
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