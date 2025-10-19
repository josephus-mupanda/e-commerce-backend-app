package com.josephus.e_commerce_backend_app.common.services;
public interface EmailSenderService {
    void sendEmail(String toEmail, String subject, String body);
}
