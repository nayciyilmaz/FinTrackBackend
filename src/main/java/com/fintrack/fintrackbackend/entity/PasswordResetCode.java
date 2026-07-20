package com.fintrack.fintrackbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "password_reset_codes")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordResetCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    @Builder.Default
    private boolean used = false;

    private String resetToken;

    private Instant resetTokenExpiryDate;

    public boolean isExpired() {
        return Instant.now().isAfter(expiryDate);
    }

    public boolean isResetTokenExpired() {
        return resetTokenExpiryDate == null || Instant.now().isAfter(resetTokenExpiryDate);
    }
}
