package com.fintrack.fintrackbackend.controller;

import com.fintrack.fintrackbackend.dto.EmailUpdateResponseDto;
import com.fintrack.fintrackbackend.dto.ForgotPasswordRequestDto;
import com.fintrack.fintrackbackend.dto.GoogleAuthRequestDto;
import com.fintrack.fintrackbackend.dto.LoginRequestDto;
import com.fintrack.fintrackbackend.dto.RefreshTokenRequestDto;
import com.fintrack.fintrackbackend.dto.ResetPasswordRequestDto;
import com.fintrack.fintrackbackend.dto.UpdateEmailRequestDto;
import com.fintrack.fintrackbackend.dto.UpdateNameRequestDto;
import com.fintrack.fintrackbackend.dto.UpdatePasswordRequestDto;
import com.fintrack.fintrackbackend.dto.UserProfileResponseDto;
import com.fintrack.fintrackbackend.dto.UserRequestDto;
import com.fintrack.fintrackbackend.dto.UserResponseDto;
import com.fintrack.fintrackbackend.dto.VerifyResetCodeRequestDto;
import com.fintrack.fintrackbackend.dto.VerifyResetCodeResponseDto;
import com.fintrack.fintrackbackend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

    @PostMapping("/api/auth/refresh")
    public ResponseEntity<UserResponseDto> refreshToken(@RequestBody @Valid RefreshTokenRequestDto request) {
        return ResponseEntity.ok(userService.refreshToken(request.getRefresh_token()));
    }

    @PostMapping("/api/auth/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequestDto request) {
        userService.sendPasswordResetCode(request.getEmail());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/auth/verify-reset-code")
    public ResponseEntity<VerifyResetCodeResponseDto> verifyResetCode(@RequestBody @Valid VerifyResetCodeRequestDto request) {
        String resetToken = userService.verifyPasswordResetCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(VerifyResetCodeResponseDto.builder().resetToken(resetToken).build());
    }

    @PostMapping("/api/auth/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequestDto request) {
        userService.resetPassword(request.getReset_token(), request.getNew_password());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/users/me")
    public ResponseEntity<UserProfileResponseDto> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getCurrentUser(userDetails.getUsername()));
    }

    @PostMapping("/api/users/me/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails userDetails) {
        userService.logout(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/users/me/name")
    public ResponseEntity<UserProfileResponseDto> updateName(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid UpdateNameRequestDto request
    ) {
        return ResponseEntity.ok(userService.updateName(userDetails.getUsername(), request.getFirst_name(), request.getLast_name()));
    }

    @PutMapping("/api/users/me/email")
    public ResponseEntity<EmailUpdateResponseDto> updateEmail(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid UpdateEmailRequestDto request
    ) {
        return ResponseEntity.ok(userService.updateEmail(userDetails.getUsername(), request.getEmail()));
    }

    @PutMapping("/api/users/me/password")
    public ResponseEntity<UserProfileResponseDto> updatePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid UpdatePasswordRequestDto request
    ) {
        return ResponseEntity.ok(userService.updatePassword(userDetails.getUsername(), request.getCurrent_password(), request.getNew_password()));
    }
}
