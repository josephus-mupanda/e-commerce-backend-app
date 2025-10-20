package com.josephus.e_commerce_backend_app.common.repositories;

import com.josephus.e_commerce_backend_app.common.models.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    long deleteByExpiryDateBefore(LocalDateTime now);

    PasswordResetToken findByToken(String token);
}
