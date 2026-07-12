package com.fintrack.fintrackbackend.service;

import com.fintrack.fintrackbackend.dto.SavingsGoalRequestDto;
import com.fintrack.fintrackbackend.dto.SavingsGoalResponseDto;
import com.fintrack.fintrackbackend.dto.SavingsGoalUpdateDto;
import com.fintrack.fintrackbackend.entity.SavingsGoal;
import com.fintrack.fintrackbackend.entity.User;
import com.fintrack.fintrackbackend.exception.BusinessException;
import com.fintrack.fintrackbackend.exception.ErrorCode;
import com.fintrack.fintrackbackend.repository.SavingsGoalRepository;
import com.fintrack.fintrackbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavingsGoalService {

    private static final Logger log = LoggerFactory.getLogger(SavingsGoalService.class);

    private final SavingsGoalRepository savingsGoalRepository;
    private final UserRepository userRepository;

    public List<SavingsGoalResponseDto> getGoals(String email) {
        log.info("Hedef listesi isteği: email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return savingsGoalRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public SavingsGoalResponseDto addGoal(String email, SavingsGoalRequestDto dto) {
        log.info("Hedef ekleme isteği: email={}, name={}", email, dto.getName());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        SavingsGoal goal = SavingsGoal.builder()
                .user(user)
                .name(dto.getName())
                .category(dto.getCategory())
                .targetAmount(dto.getTargetAmount())
                .currentAmount(BigDecimal.ZERO)
                .build();

        SavingsGoal saved = savingsGoalRepository.save(goal);
        log.info("Hedef eklendi: id={}, email={}", saved.getId(), email);

        return toResponseDto(saved);
    }

    public SavingsGoalResponseDto updateGoal(String email, Long id, SavingsGoalUpdateDto dto) {
        log.info("Hedef güncelleme isteği: email={}, id={}", email, id);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        SavingsGoal goal = savingsGoalRepository.findById(id)
                .filter(g -> g.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new BusinessException(ErrorCode.GOAL_NOT_FOUND));

        if (dto.getAddAmount() != null && dto.getAddAmount().compareTo(BigDecimal.ZERO) > 0) {
            goal.setCurrentAmount(goal.getCurrentAmount().add(dto.getAddAmount()));
        }

        if (dto.getNewTargetAmount() != null && dto.getNewTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
            goal.setTargetAmount(dto.getNewTargetAmount());
        }

        return toResponseDto(savingsGoalRepository.save(goal));
    }

    public void deleteGoal(String email, Long id) {
        log.info("Hedef silme isteği: email={}, id={}", email, id);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        SavingsGoal goal = savingsGoalRepository.findById(id)
                .filter(g -> g.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new BusinessException(ErrorCode.GOAL_NOT_FOUND));

        savingsGoalRepository.delete(goal);
    }

    private SavingsGoalResponseDto toResponseDto(SavingsGoal g) {
        return SavingsGoalResponseDto.builder()
                .id(g.getId())
                .name(g.getName())
                .category(g.getCategory())
                .targetAmount(g.getTargetAmount())
                .currentAmount(g.getCurrentAmount())
                .createdAt(g.getCreatedAt())
                .build();
    }
}
