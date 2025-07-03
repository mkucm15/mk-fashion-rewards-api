
/**
 * Service class responsible for calculating customer reward points
 * based on transaction data retrieved from the repository.
 * Supports optional date filtering and computes total and monthly reward summaries.
 */
package com.mk.rewards.service;

import com.mk.rewards.dto.RewardSummaryResponse;
import com.mk.rewards.exception.CustomerNotFoundException;
import com.mk.rewards.model.Transaction;
import com.mk.rewards.policy.RewardPolicy;
import com.mk.rewards.policy.DefaultRewardPolicy;
import com.mk.rewards.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RewardService {

    private final TransactionRepository repository;
    private final RewardPolicy rewardPolicy;

    private static final Logger log = LoggerFactory.getLogger(RewardService.class);

    public RewardService(TransactionRepository repository) {
        this.repository = repository;
        this.rewardPolicy = new DefaultRewardPolicy();
    }

    /**
     * Calculates rewards for a given customer over an optional date range.
     *
     * @param customerId the customer ID
     * @param fromDate   optional start date for filtering transactions
     * @param toDate     optional end date for filtering transactions
     * @return reward summary including customer info, total points, monthly breakdown, and transactions
     * @throws IllegalArgumentException if fromDate is after toDate
     * @throws CustomerNotFoundException if the customer ID does not exist
     */
    public RewardSummaryResponse calculateRewards(String customerId, LocalDate fromDate, LocalDate toDate) {
        log.info("Calculating rewards for customerId={} from {} to {}", customerId, fromDate, toDate);
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            log.warn("Invalid date range: fromDate {} is after toDate {}", fromDate, toDate);
            throw new IllegalArgumentException("Invalid date range: fromDate cannot be after toDate.");
        }

        List<Transaction> allCustomerTxns = repository.findByCustomerIdIgnoreCase(customerId);
        if (allCustomerTxns.isEmpty()) {
            log.warn("Customer ID '{}' not found in transaction records", customerId);
            throw new CustomerNotFoundException("No customer found with ID: " + customerId);
        }

        List<Transaction> filtered = repository.findByCustomerIdIgnoreCaseAndTransactionDateBetween(customerId, fromDate, toDate);
        if (filtered.isEmpty()) {
            log.warn("No transactions found for customerId={} in given date range", customerId);
            String customerName = allCustomerTxns.get(0).getCustomerName();

            // If no fromDate/toDate were specified, fall back to all transactions for reward calculation
            if (fromDate == null && toDate == null) {
                Map<String, Integer> monthlyPoints = calculateMonthlyPoints(allCustomerTxns);
                int totalPoints = calculateTotalPoints(monthlyPoints);

                LocalDate actualFrom = allCustomerTxns.stream().map(Transaction::getTransactionDate).min(LocalDate::compareTo).orElse(null);
                LocalDate actualTo = allCustomerTxns.stream().map(Transaction::getTransactionDate).max(LocalDate::compareTo).orElse(null);

                return new RewardSummaryResponse(customerId, customerName, actualFrom, actualTo, monthlyPoints, totalPoints, allCustomerTxns);
            }

            // Otherwise return zero rewards as filtered set is truly empty
            return new RewardSummaryResponse(customerId, customerName, fromDate, toDate, Map.of(), 0, List.of());
        }

        String customerName = filtered.get(0).getCustomerName();
        Map<String, Integer> monthlyPoints = calculateMonthlyPoints(filtered);
        int totalPoints = calculateTotalPoints(monthlyPoints);

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

    /**
     * Computes monthly reward points from a list of transactions.
     *
     * @param transactions list of filtered transactions
     * @return map of month (yyyy-MM) to earned points
     */
    private Map<String, Integer> calculateMonthlyPoints(List<Transaction> transactions) {
        Map<String, Integer> monthlyPoints = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        for (Transaction txn : transactions) {
            int points = rewardPolicy.calculate(txn.getAmount());
            String month = txn.getTransactionDate().format(formatter);
            monthlyPoints.put(month, monthlyPoints.getOrDefault(month, 0) + points);
        }
        return monthlyPoints;
    }

    /**
     * Sums total reward points from monthly breakdown.
     *
     * @param monthlyPoints map of monthly points
     * @return total reward points
     */
    private int calculateTotalPoints(Map<String, Integer> monthlyPoints) {
        return monthlyPoints.values().stream().mapToInt(Integer::intValue).sum();
    }
}