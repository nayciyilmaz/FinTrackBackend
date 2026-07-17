package com.fintrack.fintrackbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePasswordRequestDto {

    @NotBlank(message = "Mevcut şifre boş olamaz.")
    private String current_password;

    @NotBlank(message = "Yeni şifre boş olamaz.")
    @Size(min = 6, message = "Yeni şifre en az 6 karakter olmalıdır.")
    private String new_password;
}
