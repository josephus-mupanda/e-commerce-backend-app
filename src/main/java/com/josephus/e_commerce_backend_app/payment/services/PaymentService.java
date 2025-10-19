package com.josephus.e_commerce_backend_app.payment.services;

import com.josephus.e_commerce_backend_app.payment.models.Payment;

import java.util.List;

public interface PaymentService {
    List<Payment> getAllPayments();
    Payment getPaymentById(Long id);
    Payment createPayment(Payment payment);
    Payment updatePayment(Long id, Payment payment);
    void deletePayment(Long id);
}

