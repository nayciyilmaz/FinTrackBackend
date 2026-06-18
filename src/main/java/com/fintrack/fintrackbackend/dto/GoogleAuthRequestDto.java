package com.fintrack.fintrackbackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleAuthRequestDto {

    @NotBlank
    private String idToken;
}
