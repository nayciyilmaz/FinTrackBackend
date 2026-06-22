package com.fintrack.fintrackbackend.repository;

import com.fintrack.fintrackbackend.entity.Transaction;
import com.fintrack.fintrackbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserOrderByDateDescTimeDesc(User user);
}
