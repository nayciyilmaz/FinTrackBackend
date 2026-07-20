package com.fintrack.fintrackbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetPasswordRequestDto {

    @NotBlank(message = "İşlem geçersiz.")
    private String reset_token;

    @NotBlank(message = "Yeni şifre boş olamaz.")
    @Size(min = 6, message = "Yeni şifre en az 6 karakter olmalıdır.")
    private String new_password;
}
