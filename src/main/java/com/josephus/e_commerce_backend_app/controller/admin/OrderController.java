package com.josephus.com.ecommercebackend.controller.admin;
import com.josephus.e_commerce_backend_app.common.listeners.UserListener;
import com.josephus.e_commerce_backend_app.order.models.Order;
import com.josephus.com.ecommercebackend.model.UserRole;
import com.josephus.e_commerce_backend_app.model.Users;
import com.josephus.e_commerce_backend_app.order.services.OrderService;
import com.josephus.e_commerce_backend_app.user.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import  java.util.List;
@RestController
@RequestMapping("/api/admin/orders")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserListener userListener;
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getOrders();
        return ResponseEntity.ok(orders);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrder(id);
        if (order != null) {
            return ResponseEntity.ok(order);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable Long userId) {
        List<Order> orders = orderService.getOrdersByUser(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable String status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order updatedOrder) {
        logger.info("Updating order with ID: {}", id);

        Order existingOrder = orderService.getOrder(id);

        if (existingOrder != null) {

            existingOrder.setStatus(updatedOrder.getStatus());

            Order savedOrder = orderService.updateOrder(id, existingOrder);

            if (savedOrder != null) {
                logger.info("Order updated successfully with ID: {}", id);

                // Log user action using UserListener
                userListener.logUserAction(existingOrder.getUser(), "Updated order with ID: " + id);

                return ResponseEntity.ok(savedOrder);
            } else {
                logger.error("Failed to update order with ID: {}", id);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            logger.warn("Order with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {

        logger.info("Deleting order with ID: {}", id);

        Order order = orderService.getOrder(id);

        if (order != null) {

            Long userId = order.getUser().getId();

            logger.info("Customer ID in session: {}", userId);

            if (userId == null) {
                // Handle the case where the admin ID is not found in the session
                logger.error("Admin ID not found in session");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Users customer = userService.getUserById(userId);

            if (customer != null && customer.getRole() == UserRole.ADMIN) {

                orderService.deleteOrder(id);
                logger.info("order deleted successfully with ID: {}", id);

                // Log user action using UserListener
                userListener.logUserAction(customer, "Deleted order with ID: " + id);

                return ResponseEntity.noContent().build();

            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            logger.warn("Order with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }

}
