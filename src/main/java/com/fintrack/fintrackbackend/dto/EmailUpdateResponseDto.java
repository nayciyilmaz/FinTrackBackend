package com.fintrack.fintrackbackend.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailUpdateResponseDto {

    private String email;
    private String token;
    private String refreshToken;
}
