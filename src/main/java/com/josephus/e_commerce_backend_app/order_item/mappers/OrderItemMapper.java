package com.josephus.e_commerce_backend_app.order_item.mappers;

import com.josephus.e_commerce_backend_app.order_item.dtos.OrderItemDTO;
import com.josephus.e_commerce_backend_app.order_item.models.OrderItem;

public final class OrderItemMapper {
    private OrderItemMapper() {}

    public static OrderItem toEntity(OrderItemDTO.Input dto) {
        if (dto == null) return null;
        OrderItem item = new OrderItem();
        item.setQuantity(dto.quantity());
        item.setPrice(dto.price());
        // productId & orderId mapping should be done in service layer
        return item;
    }

    public static OrderItemDTO.Output toDTO(OrderItem item) {
        if (item == null) return null;
        return new OrderItemDTO.Output(
                item.getId(),
                item.getOrder() != null ? item.getOrder().getId() : null,
                item.getProduct() != null ? item.getProduct().getId() : null,
                item.getQuantity(),
                item.getPrice()
        );
    }
}

