package com.fintrack.fintrackbackend.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDto {

    private Long id;
    private String first_name;
    private String last_name;
    private String email;
    private Integer payday;
    private String token;
    private String refresh_token;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}