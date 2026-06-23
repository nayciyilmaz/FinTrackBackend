package com.fintrack.fintrackbackend.controller;

import com.fintrack.fintrackbackend.dto.TransactionRequestDto;
import com.fintrack.fintrackbackend.dto.TransactionResponseDto;
import com.fintrack.fintrackbackend.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<TransactionResponseDto>> getTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String type,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        return ResponseEntity.ok(transactionService.getTransactions(
                userDetails.getUsername(), type, startDate, endDate
        ));
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDto> addTransaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid TransactionRequestDto dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionService.addTransaction(userDetails.getUsername(), dto));
    }
}
