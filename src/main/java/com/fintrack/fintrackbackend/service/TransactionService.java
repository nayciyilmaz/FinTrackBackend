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
import java.util.List;
import java.util.stream.Collectors;

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

    public List<TransactionResponseDto> getTransactions(String email, String type, String startDate, String endDate) {
        log.info("İşlem listesi isteği: email={}, type={}, startDate={}, endDate={}", email, type, startDate, endDate);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        List<Transaction> transactions;
        if (type != null && !type.isBlank()) {
            transactions = transactionRepository.findByUserAndTypeAndDateBetweenOrderByDateDescTimeDesc(
                    user, TransactionType.valueOf(type), start, end
            );
        } else {
            transactions = transactionRepository.findByUserAndDateBetweenOrderByDateDescTimeDesc(user, start, end);
        }

        return transactions.stream().map(this::toResponseDto).collect(Collectors.toList());
    }

    public TransactionResponseDto updateTransaction(String email, Long id, TransactionRequestDto dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Transaction transaction = transactionRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND));

        transaction.setType(TransactionType.valueOf(dto.getType()));
        transaction.setCategory(dto.getCategory());
        transaction.setAmount(dto.getAmount());
        transaction.setNote(dto.getNote());
        transaction.setDate(LocalDate.parse(dto.getDate()));
        transaction.setTime(LocalTime.parse(dto.getTime()));
        transaction.setIsRecurring(dto.getRecurring());
        transaction.setIsReminder(dto.getReminder());

        return toResponseDto(transactionRepository.save(transaction));
    }

    public void deleteTransaction(String email, Long id) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Transaction transaction = transactionRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND));

        transactionRepository.delete(transaction);
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
