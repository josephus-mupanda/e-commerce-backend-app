package com.josephus.e_commerce_backend_app.payment.repositories;

import com.josephus.e_commerce_backend_app.payment.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
