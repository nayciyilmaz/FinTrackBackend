package com.fintrack.fintrackbackend.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestDto {

    @NotBlank(message = "İsim boş olamaz.")
    @Size(min = 3, message = "İsim en az 3 karakter olmalıdır.")
    private String first_name;

    @NotBlank(message = "Soyisim boş olamaz.")
    @Size(min = 3, message = "Soyisim en az 3 karakter olmalıdır.")
    private String last_name;

    @NotBlank(message = "E-posta boş olamaz.")
    @Size(min = 3, message = "E-posta en az 3 karakter olmalıdır.")
    @Email(message = "Geçerli bir e-posta adresi girin.")
    private String email;

    @NotBlank(message = "Şifre boş olamaz.")
    @Size(min = 6, message = "Şifre en az 6 karakter olmalıdır.")
    private String password;

    @NotNull(message = "Maaş günü boş olamaz.")
    @Min(value = 1, message = "Maaş günü 1-31 arasında olmalıdır.")
    @Max(value = 31, message = "Maaş günü 1-31 arasında olmalıdır.")
    private Integer payday;

    @NotNull(message = "Maaş tutarı boş olamaz.")
    @DecimalMin(value = "0.0", message = "Maaş tutarı 0 veya üstü olmalıdır.")
    private BigDecimal salary;
}