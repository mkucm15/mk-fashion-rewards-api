package com.mk.rewards.service;

import com.mk.rewards.model.Transaction;
import com.mk.rewards.policy.RewardPolicy;
import com.mk.rewards.repository.InMemoryTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class RewardServiceTest {

    private RewardService rewardService;

    @BeforeEach
    public void setup() {
        InMemoryTransactionRepository repo = new InMemoryTransactionRepository(); // has test data
        RewardPolicy policy = amount -> {
            if (amount <= 50) return 0;
            else if (amount <= 100) return (int)(amount - 50);
            else return (int)(2 * (amount - 100) + 50);
        };
        rewardService = new RewardService(repo, policy); // using constructor
    }

    @Test
    public void testCalculateRewardsForValidCustomer() {
        LocalDate from = LocalDate.of(2024, 4, 1);
        LocalDate to = LocalDate.of(2024, 6, 30);

        var response = rewardService.calculateRewards("CUST001", from, to);

        assertNotNull(response);
        assertEquals("CUST001", response.getCustomerId());
        assertEquals("Murali Krishna", response.getCustomerName());
        assertEquals(3, response.getMonthlyRewards().size());
        assertEquals(5, response.getTransactions().size());
        assertTrue(response.getTotalRewards() > 0);
    }

    @Test
    public void testCustomerNotFoundThrowsException() {
        Exception ex = assertThrows(RuntimeException.class, () -> {
            rewardService.calculateRewards("INVALID_ID", null, null);
        });

        assertTrue(ex.getMessage().contains("No transactions found"));
    }

    @Test
    public void testValidCustomerNoTransactionsInDateRange() {
        LocalDate from = LocalDate.of(2023, 1, 1);
        LocalDate to = LocalDate.of(2023, 1, 31);

        Exception ex = assertThrows(RuntimeException.class, () -> {
            rewardService.calculateRewards("CUST001", from, to);
        });

        assertTrue(ex.getMessage().contains("No transactions found"));
    }

    @Test
    public void testCalculateRewardsForSingleMonthOnly() {
        LocalDate from = LocalDate.of(2024, 4, 1);
        LocalDate to = LocalDate.of(2024, 4, 30);

        var response = rewardService.calculateRewards("CUST001", from, to);

        assertNotNull(response);
        assertEquals("CUST001", response.getCustomerId());
        assertEquals(1, response.getMonthlyRewards().size());
        assertEquals("2024-04", response.getMonthlyRewards().keySet().iterator().next());
    }

    @Test
    public void testCalculateRewardsWithNoDateFilter() {
        var response = rewardService.calculateRewards("CUST003", null, null);

        assertNotNull(response);
        assertEquals("CUST003", response.getCustomerId());
        assertFalse(response.getTransactions().isEmpty());
        assertTrue(response.getTotalRewards() > 0);
    }
}