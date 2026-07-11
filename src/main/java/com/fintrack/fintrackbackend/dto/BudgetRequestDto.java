package com.fintrack.fintrackbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BudgetRequestDto {

    @NotBlank(message = "Kategori boş olamaz.")
    private String category;

    @NotNull(message = "Limit tutarı boş olamaz.")
    @Positive(message = "Limit tutarı sıfırdan büyük olmalıdır.")
    private BigDecimal limitAmount;
}
