package com.josephus.e_commerce_backend_app.payment.mappers;
import com.josephus.e_commerce_backend_app.payment.dtos.PaymentDTO;
import com.josephus.e_commerce_backend_app.payment.models.Payment;

public final class PaymentMapper {
    private PaymentMapper() {}

    public static Payment toEntity(PaymentDTO.Input dto) {
        if (dto == null) return null;
        Payment payment = new Payment();
        payment.setPaymentMethod(dto.paymentMethod());
        // userId mapping should be done in service layer by fetching User entity
        return payment;
    }

    public static PaymentDTO.Output toDTO(Payment payment) {
        if (payment == null) return null;
        return new PaymentDTO.Output(
                payment.getId(),
                payment.getPaymentMethod(),
                payment.getUser() != null ? payment.getUser().getId() : null
        );
    }
}
