package com.fintrack.fintrackbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateNameRequestDto {

    @NotBlank(message = "İsim boş olamaz.")
    @Size(min = 3, message = "İsim en az 3 karakter olmalıdır.")
    private String first_name;

    @NotBlank(message = "Soyisim boş olamaz.")
    @Size(min = 3, message = "Soyisim en az 3 karakter olmalıdır.")
    private String last_name;
}
