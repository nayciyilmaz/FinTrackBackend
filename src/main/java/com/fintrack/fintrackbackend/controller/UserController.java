package com.fintrack.fintrackbackend.controller;

import com.fintrack.fintrackbackend.dto.GoogleAuthRequestDto;
import com.fintrack.fintrackbackend.dto.LoginRequestDto;
import com.fintrack.fintrackbackend.dto.UserRequestDto;
import com.fintrack.fintrackbackend.dto.UserResponseDto;
import com.fintrack.fintrackbackend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/auth/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody @Valid UserRequestDto user) {
        return ResponseEntity.ok(userService.registerUser(user));
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequest) {
        UserResponseDto user = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(user);
    }

    @PostMapping("/api/auth/google")
    public ResponseEntity<UserResponseDto> googleLogin(@RequestBody @Valid GoogleAuthRequestDto request) {
        return ResponseEntity.ok(userService.loginWithGoogle(request.getIdToken()));
    }
}