package com.josephus.e_commerce_backend_app.common.services;

public interface TokenBlacklistService {
    void blacklistToken(String token, long expirationMillis);

    boolean isTokenBlacklisted(String token);

    void removeToken(String token);
}
