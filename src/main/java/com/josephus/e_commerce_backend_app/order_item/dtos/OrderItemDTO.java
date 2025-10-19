package com.josephus.e_commerce_backend_app.order_item.dtos;

public final class OrderItemDTO {
    private OrderItemDTO() {}

    public record Input(
            String productId,
            int quantity,
            double price
    ) {}

    public record Output(
            String id,
            String orderId,
            String productId,
            int quantity,
            double price
    ) {}
}
