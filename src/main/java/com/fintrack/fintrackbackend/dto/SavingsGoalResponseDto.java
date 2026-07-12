package com.fintrack.fintrackbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavingsGoalResponseDto {
    private Long id;
    private String name;
    private String category;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private LocalDateTime createdAt;
}
