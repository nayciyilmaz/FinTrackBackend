package com.fintrack.fintrackbackend.repository;

import com.fintrack.fintrackbackend.entity.Budget;
import com.fintrack.fintrackbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUser(User user);
    Optional<Budget> findByUserAndCategory(User user, String category);
}
