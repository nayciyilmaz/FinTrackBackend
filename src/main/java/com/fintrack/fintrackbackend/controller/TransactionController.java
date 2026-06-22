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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

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
