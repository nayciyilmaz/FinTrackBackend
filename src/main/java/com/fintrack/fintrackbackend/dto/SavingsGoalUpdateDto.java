package com.fintrack.fintrackbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavingsGoalUpdateDto {
    private BigDecimal addAmount;
    private BigDecimal newTargetAmount;
}
