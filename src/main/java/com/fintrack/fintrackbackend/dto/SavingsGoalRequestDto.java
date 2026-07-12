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
public class SavingsGoalRequestDto {

    @NotBlank(message = "Hedef adı boş olamaz.")
    private String name;

    @NotBlank(message = "Kategori boş olamaz.")
    private String category;

    @NotNull(message = "Hedef tutarı boş olamaz.")
    @Positive(message = "Hedef tutarı sıfırdan büyük olmalıdır.")
    private BigDecimal targetAmount;
}
