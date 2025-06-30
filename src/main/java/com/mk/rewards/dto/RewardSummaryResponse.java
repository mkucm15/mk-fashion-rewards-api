package com.mk.rewards.dto;

import com.mk.rewards.model.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class RewardSummaryResponse {

    private final String customerId;
    private final String customerName;
    private final LocalDate fromDate;
    private final LocalDate toDate;
    private final Map<String, Integer> monthlyRewards;
    private final int totalRewards;
    private final List<Transaction> transactions;

    public RewardSummaryResponse(String customerId, String customerName, LocalDate fromDate, LocalDate toDate,
                                 Map<String, Integer> monthlyRewards, int totalRewards,
                                 List<Transaction> transactions) {
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

    public List<Transaction> getTransactions() {
        return transactions;
    }
}