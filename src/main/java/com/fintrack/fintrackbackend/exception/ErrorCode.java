package com.fintrack.fintrackbackend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND(1001, "Bu e-posta ile kayıtlı kullanıcı bulunamadı.", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS(1002, "Bu e-posta adresi zaten kayıtlı.", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS(1003, "Şifre hatalı.", HttpStatus.UNAUTHORIZED),
    GOOGLE_AUTH_FAILED(1004, "Google kimlik doğrulama başarısız.", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN(1005, "Geçersiz veya süresi dolmuş refresh token.", HttpStatus.UNAUTHORIZED),
    TRANSACTION_NOT_FOUND(1006, "İşlem bulunamadı.", HttpStatus.NOT_FOUND),
    BUDGET_NOT_FOUND(1007, "Bütçe bulunamadı.", HttpStatus.NOT_FOUND),
    GOAL_NOT_FOUND(1008, "Hedef bulunamadı.", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR(9000, "Sunucu hatası oluştu.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}