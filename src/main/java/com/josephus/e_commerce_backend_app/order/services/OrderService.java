package com.josephus.e_commerce_backend_app.order.services;

import com.josephus.e_commerce_backend_app.order.models.Order;
//import com.josephus.com.ecommercebackend.model.OrderStatus;

import java.util.List;

public interface OrderService {

    Order createOrder(Order order);
    Order updateOrder(String id, Order order);
    void deleteOrder(String id);
    Order getOrder(String orderId);
    List<Order> getOrders();
    List<Order> getOrdersByStatus(String status);
    List<Order> getOrdersByUser(String userId);

}
