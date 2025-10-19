package com.josephus.e_commerce_backend_app.order_item.services.impl;

import com.josephus.e_commerce_backend_app.order_item.repositories.OrderItemRepository;
import com.josephus.e_commerce_backend_app.order_item.models.OrderItem;
import com.josephus.e_commerce_backend_app.order_item.services.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;
    @Override
    public OrderItem createOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }
    @Override
    public OrderItem getOrderItem(Long orderItemId) {
        Optional<OrderItem> orderItemOptional = orderItemRepository.findById(orderItemId);
        return orderItemOptional.orElse(null);
    }
    @Override
    public void deleteOrderItem(Long orderItemId) {
        orderItemRepository.deleteById(orderItemId);
    }

    @Override
    public OrderItem updateOrderItem(Long id, OrderItem orderItem) {
        orderItem.setId(id);
        return orderItemRepository.save(orderItem);
    }

    @Override
    public List<OrderItem> getOrdersItemsByOrder(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }
}
