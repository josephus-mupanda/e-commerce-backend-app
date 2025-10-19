package com.josephus.com.ecommercebackend.listeners;

import com.josephus.com.ecommercebackend.model.Users;
import com.josephus.com.ecommercebackend.service.LogService;

import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserListener implements HttpSessionListener {
    @Autowired
    private LogService logService;

    public void logUserAction(Users user, String action) {

        logService.logAction(action, user.getEmail());
    }
}
