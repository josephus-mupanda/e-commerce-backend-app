package com.josephus.e_commerce_backend_app.order.dtos;

import com.josephus.e_commerce_backend_app.order_item.dtos.OrderItemDTO;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public final class OrderDTO {
    private OrderDTO() {}

    public record Input(
            String userId,
            String address,
            String city,
            double totalAmount,
            String status,
            String paymentMethodId,
            Set<OrderItemDTO.Input> orderItems
    ) {}

    public record Output(
            String id,
            UUID trackingId,
            String userId,
            String address,
            String city,
            LocalDateTime orderDate,
            double totalAmount,
            String status,
            String paymentMethodId,
            Set<OrderItemDTO.Output> orderItems
    ) {}
}
