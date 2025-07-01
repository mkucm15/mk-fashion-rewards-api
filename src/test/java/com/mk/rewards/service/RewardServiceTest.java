package com.mk.rewards.service;

import com.mk.rewards.policy.DefaultRewardPolicy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class RewardServiceTest {

    private RewardService rewardService;

    @BeforeEach
    public void setup() {
        rewardService = new RewardService(); // using default constructor
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
    public void testInvalidDateRangeThrowsException() {
        LocalDate from = LocalDate.of(2024, 7, 1);
        LocalDate to = LocalDate.of(2024, 6, 30);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            rewardService.calculateRewards("CUST001", from, to);
        });
        assertTrue(ex.getMessage().contains("Invalid date range"));
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

    @Test
    public void testRewardPoints_amountZero() {
        int reward = new DefaultRewardPolicy().calculate(0);
        assertEquals(0, reward, "Amount 0 should yield 0 points");
    }

    @Test
    public void testRewardPoints_amountFifty() {
        int reward = new DefaultRewardPolicy().calculate(50);
        assertEquals(0, reward, "Amount 50 should yield 0 points");
    }

    @Test
    public void testRewardPoints_amountHundred() {
        int reward = new DefaultRewardPolicy().calculate(100);
        assertEquals(50, reward, "Amount 100 should yield 50 points (1 point per dollar from 51 to 100)");
    }

    @Test
    public void testRewardPoints_amountOneTwenty() {
        int reward = new DefaultRewardPolicy().calculate(120);
        assertEquals(90, reward, "Amount 120 = 50 points (51–100) + 40 points (2x20)");
    }
}