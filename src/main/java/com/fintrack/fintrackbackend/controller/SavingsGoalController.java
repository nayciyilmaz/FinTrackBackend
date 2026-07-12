package com.fintrack.fintrackbackend.controller;

import com.fintrack.fintrackbackend.dto.SavingsGoalRequestDto;
import com.fintrack.fintrackbackend.dto.SavingsGoalResponseDto;
import com.fintrack.fintrackbackend.dto.SavingsGoalUpdateDto;
import com.fintrack.fintrackbackend.service.SavingsGoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/savings-goals")
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;

    @GetMapping
    public ResponseEntity<List<SavingsGoalResponseDto>> getGoals(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(savingsGoalService.getGoals(userDetails.getUsername()));
    }

    @PostMapping
    public ResponseEntity<SavingsGoalResponseDto> addGoal(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid SavingsGoalRequestDto dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savingsGoalService.addGoal(userDetails.getUsername(), dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavingsGoalResponseDto> updateGoal(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody SavingsGoalUpdateDto dto
    ) {
        return ResponseEntity.ok(savingsGoalService.updateGoal(userDetails.getUsername(), id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ) {
        savingsGoalService.deleteGoal(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}
