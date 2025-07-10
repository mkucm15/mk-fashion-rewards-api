/****
 * Service class responsible for calculating customer reward points
 * based on transaction data retrieved from the repository.
 * Supports optional date filtering and computes total and monthly reward summaries.
 */
package com.mk.rewards.service;

import com.mk.rewards.dto.RewardSummaryResponse;
import com.mk.rewards.dto.TransactionSummary;
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
import java.util.stream.Collectors;

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

        List<Transaction> transactions;
        if (fromDate != null && toDate != null) {
            transactions = repository.findByCustomerIdIgnoreCaseAndTransactionDateBetween(customerId, fromDate, toDate);
        } else {
            transactions = repository.findByCustomerIdIgnoreCase(customerId);
        }

        if (transactions.isEmpty()) {
            log.warn("No transactions found for customerId={} in given context", customerId);
            throw new CustomerNotFoundException("No transactions found for customer ID: " + customerId);
        }

        String customerName = transactions.get(0).getCustomerName();
        Map<String, Integer> monthlyPoints = calculateMonthlyPoints(transactions);
        int totalPoints = calculateTotalPoints(monthlyPoints);

        // Determine actual from/to date range based on data if not provided
        LocalDate actualFrom = fromDate;
        LocalDate actualTo = toDate;
        if (fromDate == null || toDate == null) {
            List<LocalDate> dates = transactions.stream()
                .map(Transaction::getTransactionDate)
                .sorted()
                .toList();
            if (actualFrom == null) actualFrom = dates.get(0);
            if (actualTo == null) actualTo = dates.get(dates.size() - 1);
        }

//        List<TransactionSummary> txnSummaries = transactions.stream()
//            .map(txn -> new TransactionSummary(txn.getAmount(), txn.getTransactionDate()))
//            .toList();

        List<TransactionSummary> txnSummaries = (fromDate != null && toDate != null)
                ? transactions.stream()
                .map(txn -> new TransactionSummary(txn.getAmount(), txn.getTransactionDate()))
                .toList()
                : null;
        log.debug("Reward calculation complete. Total points: {}", totalPoints);
        return new RewardSummaryResponse(
                customerId,
                customerName,
                fromDate,
                toDate,
                monthlyPoints,
                totalPoints,
                txnSummaries
        );
    }

    /**
     * Computes monthly reward points from a list of transactions.
     *
     * @param transactions list of filtered transactions
     * @return map of month (yyyy-MM) to earned points
     */
    private Map<String, Integer> calculateMonthlyPoints(List<Transaction> transactions) {
        return transactions.stream()
            .collect(Collectors.groupingBy(
                txn -> java.time.YearMonth.from(txn.getTransactionDate()).toString(),
                LinkedHashMap::new,
                Collectors.summingInt(txn -> rewardPolicy.calculate(txn.getAmount()))
            ));
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