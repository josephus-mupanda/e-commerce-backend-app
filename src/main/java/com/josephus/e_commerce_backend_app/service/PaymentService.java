package com.josephus.com.ecommercebackend.service;

import com.josephus.com.ecommercebackend.model.Payment;

import java.util.List;

public interface PaymentService {
    List<Payment> getAllPayments();
    Payment getPaymentById(Long id);
    Payment createPayment(Payment payment);
    Payment updatePayment(Long id, Payment payment);
    void deletePayment(Long id);
}

