package com.fintrack.fintrackbackend.repository;

import com.fintrack.fintrackbackend.entity.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Long> {
    Optional<PasswordResetCode> findByUserIdAndCodeAndUsedFalse(Long userId, String code);
    Optional<PasswordResetCode> findByResetTokenAndUsedTrue(String resetToken);
    void deleteByUserId(Long userId);
}
