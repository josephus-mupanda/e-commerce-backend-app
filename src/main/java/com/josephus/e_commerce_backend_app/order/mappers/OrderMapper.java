package com.josephus.e_commerce_backend_app.order.mappers;

import com.josephus.e_commerce_backend_app.order.dtos.OrderDTO;
import com.josephus.e_commerce_backend_app.order.models.Order;

import java.util.stream.Collectors;

public final class OrderMapper {
    private OrderMapper() {}

    public static Order toEntity(OrderDTO.Input dto) {
        if (dto == null) return null;
        Order order = new Order();
        order.setAddress(dto.address());
        order.setCity(dto.city());
        order.setTotalAmount(dto.totalAmount());
        order.setStatus(dto.status());
        // userId & paymentMethodId mapping should be done in service layer
        // orderItems mapping will also be done in service layer
        return order;
    }

    public static OrderDTO.Output toDTO(Order order) {
        if (order == null) return null;
        return new OrderDTO.Output(
                order.getId(),
                order.getTrackingId(),
                order.getUser() != null ? order.getUser().getId() : null,
                order.getAddress(),
                order.getCity(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getPaymentMethod() != null ? order.getPaymentMethod().getId() : null,
                order.getOrderItems() != null ? order.getOrderItems().stream().map(OrderItemMapper::toDTO).collect(Collectors.toSet()) : null
        );
    }
}

