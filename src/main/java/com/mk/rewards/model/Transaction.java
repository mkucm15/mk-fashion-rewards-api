package com.mk.rewards.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.time.LocalDate;

/**
 * Entity representing a transaction made by a customer.
 * Used for reward point calculation based on purchase amount and date.
 */
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @Column(name = "transaction_id")
    private String transactionId;
    @Column(name = "customer_id")
    private String customerId;
    @Column(name = "customer_name")
    private String customerName;
    @Column(name = "amount")
    private double amount;
    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    /**
     * Default constructor required by JPA.
     */
    protected Transaction() {
        // for JPA
    }

    /**
     * Constructs a transaction record.
     *
     * @param transactionId   unique transaction identifier
     * @param customerId      identifier of the customer
     * @param customerName    name of the customer
     * @param amount          amount spent in the transaction
     * @param transactionDate date of the transaction
     */
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
