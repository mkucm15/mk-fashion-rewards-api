package com.mk.rewards.model;

import java.time.LocalDate;

public class Transaction {
    private final String transactionId;
    private final String customerId;
    private final String customerName;
    private final double amount;
    private final LocalDate transactionDate;

    public Transaction(String transactionId, String customerId, String customerName, double amount, LocalDate transactionDate) {
        this.transactionId = transactionId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }
}
