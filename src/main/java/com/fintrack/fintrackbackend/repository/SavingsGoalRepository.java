package com.fintrack.fintrackbackend.repository;

import com.fintrack.fintrackbackend.entity.SavingsGoal;
import com.fintrack.fintrackbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {
    List<SavingsGoal> findByUserOrderByCreatedAtDesc(User user);
}
