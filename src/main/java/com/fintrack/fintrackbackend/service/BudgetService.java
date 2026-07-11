package com.fintrack.fintrackbackend.service;

import com.fintrack.fintrackbackend.dto.BudgetRequestDto;
import com.fintrack.fintrackbackend.dto.BudgetResponseDto;
import com.fintrack.fintrackbackend.entity.Budget;
import com.fintrack.fintrackbackend.entity.User;
import com.fintrack.fintrackbackend.exception.BusinessException;
import com.fintrack.fintrackbackend.exception.ErrorCode;
import com.fintrack.fintrackbackend.repository.BudgetRepository;
import com.fintrack.fintrackbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private static final Logger log = LoggerFactory.getLogger(BudgetService.class);

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    public List<BudgetResponseDto> getBudgets(String email) {
        log.info("Bütçe listesi isteği: email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return budgetRepository.findByUser(user).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BudgetResponseDto> saveBudgets(String email, List<BudgetRequestDto> dtos) {
        log.info("Bütçe kaydetme isteği: email={}, adet={}", email, dtos.size());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<Budget> existingBudgets = budgetRepository.findByUser(user);
        Map<String, Budget> existingMap = existingBudgets.stream()
                .collect(Collectors.toMap(Budget::getCategory, b -> b));

        List<String> incomingCategories = dtos.stream()
                .map(BudgetRequestDto::getCategory)
                .toList();

        List<Budget> toDelete = existingBudgets.stream()
                .filter(b -> !incomingCategories.contains(b.getCategory()))
                .toList();
        budgetRepository.deleteAll(toDelete);

        List<Budget> savedBudgets = dtos.stream().map(dto -> {
            Budget existing = existingMap.get(dto.getCategory());
            if (existing != null) {
                existing.setLimitAmount(dto.getLimitAmount());
                return budgetRepository.save(existing);
            } else {
                Budget budget = Budget.builder()
                        .user(user)
                        .category(dto.getCategory())
                        .limitAmount(dto.getLimitAmount())
                        .build();
                return budgetRepository.save(budget);
            }
        }).toList();

        log.info("Bütçeler kaydedildi: email={}, adet={}", email, savedBudgets.size());

        return savedBudgets.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    private BudgetResponseDto toResponseDto(Budget b) {
        return BudgetResponseDto.builder()
                .id(b.getId())
                .category(b.getCategory())
                .limitAmount(b.getLimitAmount())
                .createdAt(b.getCreatedAt())
                .build();
    }
}
