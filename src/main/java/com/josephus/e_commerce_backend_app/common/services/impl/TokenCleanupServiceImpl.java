package com.josephus.e_commerce_backend_app.common.services.impl;

import com.josephus.e_commerce_backend_app.common.repositories.ConfirmationTokenRepository;
import com.josephus.e_commerce_backend_app.common.repositories.PasswordResetTokenRepository;
import com.josephus.e_commerce_backend_app.common.models.ConfirmationToken;
import com.josephus.e_commerce_backend_app.common.models.PasswordResetToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
        List<ConfirmationToken> tokens = confirmationTokenRepository.findAll();
        Date now = new Date();

        for (ConfirmationToken token : tokens) {
            if (token.getExpiryDate().before(now)) {
                confirmationTokenRepository.delete(token);
            }
        }
    }

    private void cleanUpExpiredPasswordResetTokens() {
        List<PasswordResetToken> tokens = passwordResetTokenRepository.findAll();
        Date now = new Date();

        for (PasswordResetToken token : tokens) {
            if (token.getExpiryDate().before(now)) {
                passwordResetTokenRepository.delete(token);
            }
        }
    }
}
