package com.fintrack.fintrackbackend.service;

import com.fintrack.fintrackbackend.dto.TransactionRequestDto;
import com.fintrack.fintrackbackend.dto.TransactionResponseDto;
import com.fintrack.fintrackbackend.entity.Transaction;
import com.fintrack.fintrackbackend.entity.TransactionType;
import com.fintrack.fintrackbackend.entity.User;
import com.fintrack.fintrackbackend.exception.BusinessException;
import com.fintrack.fintrackbackend.exception.ErrorCode;
import com.fintrack.fintrackbackend.repository.TransactionRepository;
import com.fintrack.fintrackbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionResponseDto addTransaction(String email, TransactionRequestDto dto) {
        log.info("İşlem ekleme isteği: email={}, type={}", email, dto.getType());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Transaction transaction = Transaction.builder()
                .user(user)
                .type(TransactionType.valueOf(dto.getType()))
                .category(dto.getCategory())
                .amount(dto.getAmount())
                .note(dto.getNote())
                .date(LocalDate.parse(dto.getDate()))
                .time(LocalTime.parse(dto.getTime()))
                .isRecurring(dto.getRecurring())
                .isReminder(dto.getReminder())
                .build();

        Transaction saved = transactionRepository.save(transaction);
        log.info("İşlem eklendi: id={}, email={}", saved.getId(), email);

        return toResponseDto(saved);
    }

    private TransactionResponseDto toResponseDto(Transaction t) {
        return TransactionResponseDto.builder()
                .id(t.getId())
                .type(t.getType())
                .category(t.getCategory())
                .amount(t.getAmount())
                .note(t.getNote())
                .date(t.getDate().toString())
                .time(t.getTime().toString())
                .recurring(t.getIsRecurring())
                .reminder(t.getIsReminder())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
