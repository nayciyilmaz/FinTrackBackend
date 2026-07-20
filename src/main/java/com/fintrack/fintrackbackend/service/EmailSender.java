package com.fintrack.fintrackbackend.service;

public interface EmailSender {
    void sendPasswordResetCode(String to, String code);
}
