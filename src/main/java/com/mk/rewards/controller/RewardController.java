package com.mk.rewards.controller;

import com.mk.rewards.dto.RewardSummaryResponse;
import com.mk.rewards.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


/**
 * REST controller that exposes endpoints for retrieving customer reward summaries.
 * Supports optional filtering by date range.
 * Delegates business logic to RewardService.
 */
@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    @Autowired
    private RewardService rewardService;

    @GetMapping("/{customerId}")
    /**
     * Retrieves the reward summary for a specific customer.
     *
     * @param customerId the customer ID
     * @param fromDate optional start date for filtering transactions (yyyy-MM-dd)
     * @param toDate optional end date for filtering transactions (yyyy-MM-dd)
     * @return reward summary for the specified customer and date range
     */
    public RewardSummaryResponse getCustomerRewards(
            @PathVariable String customerId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return rewardService.calculateRewards(customerId, fromDate, toDate);
    }
}