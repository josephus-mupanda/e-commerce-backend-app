package com.josephus.e_commerce_backend_app.payment.dtos;

public final class PaymentDTO {
    private PaymentDTO() {}

    public record Input(
            String paymentMethod,
            String userId
    ) {}

    public record Output(
            String id,
            String paymentMethod,
            String userId
    ) {}
}

