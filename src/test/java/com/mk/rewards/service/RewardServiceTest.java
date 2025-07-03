/**
 * Unit tests for RewardService.
 * These tests validate the reward point calculation logic for various customer transaction scenarios.
 * The repository is mocked to isolate the service logic.
 */
package com.mk.rewards.service;

import com.mk.rewards.policy.DefaultRewardPolicy;
import org.mockito.Mockito;
import com.mk.rewards.repository.TransactionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

import com.mk.rewards.model.Transaction;
import java.util.List;

public class RewardServiceTest {

    private RewardService rewardService;
    private TransactionRepository mockRepository;

    @BeforeEach
    public void setup() {
        mockRepository = Mockito.mock(TransactionRepository.class);
        rewardService = new RewardService(mockRepository);
    }

    @Test
    public void testCalculateRewardsForValidCustomer() {
        LocalDate from = LocalDate.of(2024, 4, 1);
        LocalDate to = LocalDate.of(2024, 6, 30);

        List<Transaction> mockTransactions = List.of(
            new Transaction("TXN1001", "CUST001", "Murali Krishna", 120.0, LocalDate.of(2024, 4, 15)),
            new Transaction("TXN1002", "CUST001", "Murali Krishna", 90.0, LocalDate.of(2024, 5, 10)),
            new Transaction("TXN1003", "CUST001", "Murali Krishna", 130.0, LocalDate.of(2024, 6, 5)),
            new Transaction("TXN1004", "CUST001", "Murali Krishna", 49.0, LocalDate.of(2024, 4, 25)),
            new Transaction("TXN1005", "CUST001", "Murali Krishna", 100.0, LocalDate.of(2024, 6, 18))
        );
        Mockito.when(mockRepository.findByCustomerIdIgnoreCase("CUST001")).thenReturn(mockTransactions);
        Mockito.when(mockRepository.findByCustomerIdIgnoreCaseAndTransactionDateBetween("CUST001", from, to)).thenReturn(mockTransactions);

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

        assertTrue(ex.getMessage().contains("No customer found"));
    }

    @Test
    public void testValidCustomerNoTransactionsInDateRange() {
        LocalDate from = LocalDate.of(2023, 1, 1);
        LocalDate to = LocalDate.of(2023, 1, 31);

        List<Transaction> allTxns = List.of(
            new Transaction("TXN1001", "CUST001", "Murali Krishna", 120.0, LocalDate.of(2024, 4, 15))
        );
        Mockito.when(mockRepository.findByCustomerIdIgnoreCase("CUST001")).thenReturn(allTxns);
        Mockito.when(mockRepository.findByCustomerIdIgnoreCaseAndTransactionDateBetween("CUST001", from, to)).thenReturn(List.of());

        var response = rewardService.calculateRewards("CUST001", from, to);

        assertNotNull(response);
        assertEquals("CUST001", response.getCustomerId());
        assertEquals(0, response.getTotalRewards());
        assertTrue(response.getMonthlyRewards().isEmpty());
        assertTrue(response.getTransactions().isEmpty());
    }

    @Test
    public void testCalculateRewardsForSingleMonthOnly() {
        LocalDate from = LocalDate.of(2024, 4, 1);
        LocalDate to = LocalDate.of(2024, 4, 30);

        List<Transaction> aprilTxns = List.of(
            new Transaction("TXN1001", "CUST001", "Murali Krishna", 120.0, LocalDate.of(2024, 4, 15))
        );
        Mockito.when(mockRepository.findByCustomerIdIgnoreCase("CUST001")).thenReturn(aprilTxns);
        Mockito.when(mockRepository.findByCustomerIdIgnoreCaseAndTransactionDateBetween("CUST001", from, to)).thenReturn(aprilTxns);

        var response = rewardService.calculateRewards("CUST001", from, to);

        assertNotNull(response);
        assertEquals("CUST001", response.getCustomerId());
        assertEquals(1, response.getMonthlyRewards().size());
        assertEquals("2024-04", response.getMonthlyRewards().keySet().iterator().next());
    }

    @Test
    public void testCalculateRewardsWithNoDateFilter() {
        List<Transaction> mockTransactions = List.of(
            new Transaction("TXN1006", "CUST003", "Ram Prasad", 75.0, LocalDate.of(2024, 4, 22)),
            new Transaction("TXN1007", "CUST003", "Ram Prasad", 101.0, LocalDate.of(2024, 5, 11))
        );
        Mockito.when(mockRepository.findByCustomerIdIgnoreCase("CUST003")).thenReturn(mockTransactions);
        Mockito.when(mockRepository.findByCustomerIdIgnoreCaseAndTransactionDateBetween("CUST003", null, null)).thenReturn(List.of());

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