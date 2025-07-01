package com.mk.rewards.controller;

import com.mk.rewards.dto.RewardSummaryResponse;
import com.mk.rewards.service.RewardService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RewardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RewardService rewardService;

    @Test
    public void testGetRewards_success() throws Exception {
        RewardSummaryResponse mockResponse = new RewardSummaryResponse(
                "CUST001",
                "Murali Krishna",
                LocalDate.of(2024, 4, 1),
                LocalDate.of(2024, 6, 30),
                Map.of("2024-04", 90, "2024-05", 40, "2024-06", 110),
                240,
                Collections.emptyList()
        );

        Mockito.when(rewardService.calculateRewards(Mockito.eq("CUST001"),
                        Mockito.any(LocalDate.class), Mockito.any(LocalDate.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/rewards/CUST001")
                        .param("fromDate", "2024-04-01")
                        .param("toDate", "2024-06-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("CUST001"))
                .andExpect(jsonPath("$.customerName").value("Murali Krishna"))
                .andExpect(jsonPath("$.totalRewards").value(240));
    }

    @Test
    public void testGetRewards_customerNotFound() throws Exception {
        Mockito.when(rewardService.calculateRewards(Mockito.eq("INVALID"), Mockito.any(), Mockito.any()))
                .thenThrow(new RuntimeException("No transactions found"));

        mockMvc.perform(get("/api/rewards/INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No transactions found"));
    }
}