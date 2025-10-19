package com.josephus.com.ecommercebackend.service;
public interface EmailSenderService {
    void sendEmail(String toEmail, String subject, String body);
}
