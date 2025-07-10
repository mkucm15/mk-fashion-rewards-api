package com.mk.rewards.dto;

import java.time.LocalDate;

/**
 * Represents a simplified view of a transaction for API response.
 */
public class TransactionSummary {
    private Double amount;
    private LocalDate transactionDate;

    public TransactionSummary(Double amount, LocalDate transactionDate) {
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    public Double getAmount() {
        return amount;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }
}