package com.mk.rewards.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mk.rewards.dto.TransactionSummary;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DTO representing the summary of reward points earned by a customer.
 * Includes monthly and total reward calculations along with transaction history and optional date filtering.
 */
/**
 * Represents a response object containing the reward summary for a customer.
 * It includes reward points aggregated by month, the total rewards earned,
 * and a simplified view of the transactions that contributed to the rewards.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RewardSummaryResponse {

    private final String customerId;
    private final String customerName;
    private final LocalDate fromDate;
    private final LocalDate toDate;
    private final Map<String, Integer> monthlyRewards;
    private final int totalRewards;
    private final List<TransactionSummary> transactions;

    /**
     * Constructs a reward summary for the given customer and transaction data.
     *
     * @param customerId       customer ID
     * @param customerName     name of the customer
     * @param fromDate         optional start date of the reward period
     * @param toDate           optional end date of the reward period
     * @param monthlyRewards   reward points earned per month
     * @param totalRewards     total reward points earned
     * @param transactions     list of summarized transactions included in the reward calculation
     */
    public RewardSummaryResponse(String customerId, String customerName, LocalDate fromDate, LocalDate toDate,
                                 Map<String, Integer> monthlyRewards, int totalRewards,
                                 List<TransactionSummary> transactions) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.monthlyRewards = monthlyRewards;
        this.totalRewards = totalRewards;
        this.transactions = transactions;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public Map<String, Integer> getMonthlyRewards() {
        return monthlyRewards;
    }

    public int getTotalRewards() {
        return totalRewards;
    }
    public List<TransactionSummary> getTransactions() {
        return transactions;
    }
}