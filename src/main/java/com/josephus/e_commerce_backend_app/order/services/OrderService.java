package com.josephus.e_commerce_backend_app.order.services;

import com.josephus.e_commerce_backend_app.order.models.Order;
//import com.josephus.com.ecommercebackend.model.OrderStatus;

import java.util.List;

public interface OrderService {

    Order createOrder(Order order);
    Order updateOrder(Long id, Order order);
    void deleteOrder(Long id);
    Order getOrder(Long orderId);
    List<Order> getOrders();
    List<Order> getOrdersByStatus(String status);
    List<Order> getOrdersByUser(Long userId);

}
