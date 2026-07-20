package com.fintrack.fintrackbackend.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyResetCodeResponseDto {

    private String resetToken;
}
