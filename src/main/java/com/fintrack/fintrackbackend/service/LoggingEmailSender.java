package com.fintrack.fintrackbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggingEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(LoggingEmailSender.class);

    @Override
    public void sendPasswordResetCode(String to, String code) {
        log.info("Şifre sıfırlama kodu: email={}, code={}", to, code);
    }
}
