package com.fintrack.fintrackbackend.dto;

import com.fintrack.fintrackbackend.entity.TransactionType;
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
public class TransactionResponseDto {
    private Long id;
    private TransactionType type;
    private String category;
    private BigDecimal amount;
    private String note;
    private String date;
    private String time;
    private Boolean recurring;
    private Boolean reminder;
    private LocalDateTime createdAt;
}
