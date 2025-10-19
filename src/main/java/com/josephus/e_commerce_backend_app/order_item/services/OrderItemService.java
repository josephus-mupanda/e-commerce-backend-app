package com.josephus.e_commerce_backend_app.order_item.services;

import com.josephus.e_commerce_backend_app.order_item.models.OrderItem;

import java.util.List;

public interface OrderItemService {
    OrderItem createOrderItem(OrderItem orderItem);
    OrderItem getOrderItem(Long orderItemId);
    void deleteOrderItem(Long orderItemId);
    OrderItem updateOrderItem(Long id, OrderItem orderItem);
    List<OrderItem> getOrdersItemsByOrder(Long orderId);
}

