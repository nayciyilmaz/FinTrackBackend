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
public class BudgetResponseDto {
    private Long id;
    private String category;
    private BigDecimal limitAmount;
    private LocalDateTime createdAt;
}
