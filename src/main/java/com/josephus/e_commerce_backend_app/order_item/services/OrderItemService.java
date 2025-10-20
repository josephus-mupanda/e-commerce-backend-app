package com.josephus.e_commerce_backend_app.order_item.services;

import com.josephus.e_commerce_backend_app.order_item.models.OrderItem;

import java.util.List;

public interface OrderItemService {
    OrderItem createOrderItem(OrderItem orderItem);
    OrderItem getOrderItem(String orderItemId);
    void deleteOrderItem(String orderItemId);
    OrderItem updateOrderItem(String id, OrderItem orderItem);
    List<OrderItem> getOrdersItemsByOrder(String orderId);
}

