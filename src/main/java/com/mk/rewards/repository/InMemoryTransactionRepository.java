package com.mk.rewards.repository;

import com.mk.rewards.model.Transaction;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Repository
public class InMemoryTransactionRepository {

    public List<Transaction> getAllTransactions() {
        return Arrays.asList(
            new Transaction("TXN1001", "CUST001", "Murali Krishna", 120.0, LocalDate.of(2024, 4, 15)),
            new Transaction("TXN1002", "CUST001", "Murali Krishna", 90.0, LocalDate.of(2024, 5, 10)),
            new Transaction("TXN1003", "CUST001", "Murali Krishna", 130.0, LocalDate.of(2024, 6, 5)),
            new Transaction("TXN1004", "CUST001", "Murali Krishna", 49.0, LocalDate.of(2024, 4, 25)),  // No reward
            new Transaction("TXN1005", "CUST001", "Murali Krishna", 100.0, LocalDate.of(2024, 6, 18)),
            new Transaction("TXN1006", "CUST003", "Ram Prasad", 75.0, LocalDate.of(2024, 4, 22)),
            new Transaction("TXN1007", "CUST003", "Ram Prasad", 101.0, LocalDate.of(2024, 5, 11)),
            new Transaction("TXN1008", "CUST004", "Sita Devi", 55.0, LocalDate.of(2024, 5, 9)),
            new Transaction("TXN1009", "CUST004", "Sita Devi", 200.0, LocalDate.of(2024, 6, 30))
        );
    }
    public List<Transaction> getTransactions(String customerId, LocalDate from, LocalDate to) {
        return getAllTransactions().stream()
                .filter(txn -> txn.getCustomerId().equalsIgnoreCase(customerId))
                .filter(txn -> (from == null || !txn.getTransactionDate().isBefore(from)) &&
                               (to == null || !txn.getTransactionDate().isAfter(to)))
                .toList();
    }
}
