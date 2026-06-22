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
public class TransactionRequestDto {

    @NotBlank(message = "İşlem türü boş olamaz.")
    private String type;

    @NotBlank(message = "Kategori boş olamaz.")
    private String category;

    @NotNull(message = "Tutar boş olamaz.")
    @Positive(message = "Tutar sıfırdan büyük olmalıdır.")
    private BigDecimal amount;

    private String note;

    @NotBlank(message = "Tarih boş olamaz.")
    private String date;

    @NotBlank(message = "Saat boş olamaz.")
    private String time;

    @Builder.Default
    private Boolean recurring = false;

    @Builder.Default
    private Boolean reminder = false;
}
