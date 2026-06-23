package com.fintrack.fintrackbackend.repository;

import com.fintrack.fintrackbackend.entity.Transaction;
import com.fintrack.fintrackbackend.entity.TransactionType;
import com.fintrack.fintrackbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserOrderByDateDescTimeDesc(User user);
    List<Transaction> findByUserAndDateBetweenOrderByDateDescTimeDesc(User user, LocalDate startDate, LocalDate endDate);
    List<Transaction> findByUserAndTypeAndDateBetweenOrderByDateDescTimeDesc(User user, TransactionType type, LocalDate startDate, LocalDate endDate);
}
