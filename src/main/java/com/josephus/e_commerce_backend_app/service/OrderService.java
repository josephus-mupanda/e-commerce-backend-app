package com.josephus.com.ecommercebackend.service;

import com.josephus.com.ecommercebackend.model.Order;
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
