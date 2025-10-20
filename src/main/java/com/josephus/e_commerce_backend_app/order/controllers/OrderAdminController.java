package com.josephus.e_commerce_backend_app.order.controllers;

import com.josephus.e_commerce_backend_app.common.enums.UserType;
import com.josephus.e_commerce_backend_app.common.exceptions.NotFoundException;
import com.josephus.e_commerce_backend_app.common.listeners.UserListener;
import com.josephus.e_commerce_backend_app.order.services.OrderService;
import com.josephus.e_commerce_backend_app.user.models.User;
import com.josephus.e_commerce_backend_app.user.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.josephus.e_commerce_backend_app.common.annotations.IsAuthenticated;
import com.josephus.e_commerce_backend_app.common.exceptions.ForbiddenException;
import com.josephus.e_commerce_backend_app.common.exceptions.UnauthorizedException;
import com.josephus.e_commerce_backend_app.order.dtos.OrderDTO;
import com.josephus.e_commerce_backend_app.order.mappers.OrderMapper;
import com.josephus.e_commerce_backend_app.order.models.Order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/orders")
@Tag(name = "Admin - Orders", description = "Endpoints for managing orders by admin users")
public class OrderAdminController {

    private static final Logger logger = LoggerFactory.getLogger(OrderAdminController.class);
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    @Autowired
    private UserListener userListener;

    /**
     * Get all orders (Admin only)
     */
    @IsAuthenticated
    @GetMapping
    @Operation(summary = "Get all orders", description = "Retrieve a list of all orders (Admin only).")
    public List<OrderDTO.Output> getAllOrders(@RequestHeader("Authorization") String token) {
        User admin = getAdminUser(token);
        List<Order> orders = orderService.getOrders();

        userListener.logUserAction(admin, "Viewed all orders");
        logger.info("Admin '{}' viewed all orders", admin.getUsername());

        return orders.stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific order by ID
     */
    @IsAuthenticated
    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Retrieve a specific order by ID (Admin only).")
    public OrderDTO.Output getOrderById(@PathVariable String id, @RequestHeader("Authorization") String token) {
        User admin = getAdminUser(token);
        Order order = orderService.getOrder(id);

        if (order == null)
            throw new NotFoundException("Order not found with ID: " + id);

        userListener.logUserAction(admin, "Viewed order with ID: " + id);
        logger.info("Admin '{}' viewed order ID: {}", admin.getUsername(), id);

        return OrderMapper.toDTO(order);
    }

    /**
     * Update order status
     */
    @IsAuthenticated
    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status", description = "Update the status of an order (Admin only).")
    public OrderDTO.Output updateOrderStatus(
            @PathVariable String id,
            @RequestParam String status,
            @RequestHeader("Authorization") String token) {

        User admin = getAdminUser(token);
        Order order = orderService.getOrder(id);

        if (order == null)
            throw new NotFoundException("Order not found with ID: " + id);

        order.setStatus(status);
        Order updatedOrder = orderService.updateOrder(id,order);

        userListener.logUserAction(admin, "Updated order status for ID: " + id);
        logger.info("Admin '{}' updated order ID: {} to status '{}'", admin.getUsername(), id, status);

        return OrderMapper.toDTO(updatedOrder);
    }

    /**
     * Delete order by ID
     */
    @IsAuthenticated
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete order", description = "Delete an order by ID (Admin only).")
    public void deleteOrder(@PathVariable String id, @RequestHeader("Authorization") String token) {
        User admin = getAdminUser(token);
        Order order = orderService.getOrder(id);

        if (order == null)
            throw new NotFoundException("Order not found with ID: " + id);

        orderService.deleteOrder(id);

        userListener.logUserAction(admin, "Deleted order with ID: " + id);
        logger.info("Admin '{}' deleted order ID: {}", admin.getUsername(), id);
    }

    /**
     * Extract and validate the admin user
     */
    private User getAdminUser(String token) {
        if (token == null || token.isBlank())
            throw new UnauthorizedException("Missing or invalid authorization token");

        User user = userService.getUserFromToken(token);
        if (user == null)
            throw new UnauthorizedException("Invalid or expired token");
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> {
                    try {
                        // convert role name (String) to enum safely
                        return UserType.valueOf(role.getName().toUpperCase()) == UserType.ADMIN;
                    } catch (IllegalArgumentException e) {
                        return false; // ignore roles not matching enum
                    }
                });

        if (!isAdmin) {
            throw new ForbiddenException("Access denied: Admins only");
        }
        return user;
    }
}
