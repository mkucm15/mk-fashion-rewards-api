package com.mk.rewards.repository;

import com.mk.rewards.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for accessing Transaction data using Spring Data JPA.
 * Provides methods to retrieve transactions by customer ID and optional date filtering.
 */
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByCustomerIdIgnoreCaseAndTransactionDateBetween(String customerId, LocalDate from, LocalDate to);

    List<Transaction> findByCustomerIdIgnoreCase(String customerId);
}
