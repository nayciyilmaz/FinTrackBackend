package com.fintrack.fintrackbackend.controller;

import com.fintrack.fintrackbackend.dto.BudgetRequestDto;
import com.fintrack.fintrackbackend.dto.BudgetResponseDto;
import com.fintrack.fintrackbackend.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    public ResponseEntity<List<BudgetResponseDto>> getBudgets(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(budgetService.getBudgets(userDetails.getUsername()));
    }

    @PutMapping
    public ResponseEntity<List<BudgetResponseDto>> saveBudgets(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid List<BudgetRequestDto> dtos
    ) {
        return ResponseEntity.ok(budgetService.saveBudgets(userDetails.getUsername(), dtos));
    }
}
