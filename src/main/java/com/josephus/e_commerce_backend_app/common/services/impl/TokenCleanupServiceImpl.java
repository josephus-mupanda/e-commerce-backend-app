package com.josephus.e_commerce_backend_app.common.services.impl;

import com.josephus.e_commerce_backend_app.common.repositories.ConfirmationTokenRepository;
import com.josephus.e_commerce_backend_app.common.repositories.PasswordResetTokenRepository;
import com.josephus.e_commerce_backend_app.common.services.TokenCleanupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TokenCleanupServiceImpl implements TokenCleanupService {
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Scheduled(fixedRate = 86400000)  // Run once a day
    public void cleanUpExpiredTokens() {
        cleanUpExpiredConfirmationTokens();
        cleanUpExpiredPasswordResetTokens();
    }

    private void cleanUpExpiredConfirmationTokens() {
        confirmationTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }

    private void cleanUpExpiredPasswordResetTokens() {
       passwordResetTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}
