package com.fintrack.fintrackbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateEmailRequestDto {

    @NotBlank(message = "E-posta boş olamaz.")
    @Size(min = 3, message = "E-posta en az 3 karakter olmalıdır.")
    @Email(message = "Geçerli bir e-posta adresi girin.")
    private String email;
}
