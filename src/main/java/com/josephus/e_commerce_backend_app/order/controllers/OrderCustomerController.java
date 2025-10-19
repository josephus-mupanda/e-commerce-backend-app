package com.josephus.e_commerce_backend_app.order.controllers;

import com.josephus.e_commerce_backend_app.common.annotations.IsAuthenticated;
import com.josephus.e_commerce_backend_app.common.enums.UserType;
import com.josephus.e_commerce_backend_app.common.exceptions.ForbiddenException;
import com.josephus.e_commerce_backend_app.common.exceptions.NotFoundException;
import com.josephus.e_commerce_backend_app.common.exceptions.UnauthorizedException;
import com.josephus.e_commerce_backend_app.common.listeners.UserListener;
import com.josephus.e_commerce_backend_app.order.dtos.OrderDTO;
import com.josephus.e_commerce_backend_app.order.mappers.OrderMapper;
import com.josephus.e_commerce_backend_app.order.models.Order;
import com.josephus.e_commerce_backend_app.order.services.OrderService;
import com.josephus.e_commerce_backend_app.user.models.User;
import com.josephus.e_commerce_backend_app.user.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customer/orders")
@Tag(name = "Customer - Orders", description = "Endpoints for customers to manage their orders")
public class OrderCustomerController {

    private static final Logger logger = LoggerFactory.getLogger(OrderCustomerController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserListener userListener;

    /**
     * Get all orders (for authenticated customer)
     */
    @IsAuthenticated
    @GetMapping
    @Operation(summary = "Get all orders", description = "Retrieve all orders for the authenticated customer.")
    public List<OrderDTO.Output> getCustomerOrders(@RequestHeader("Authorization") String token) {
        User user = getCustomerUser(token);

        List<Order> orders = orderService.getOrdersByUser(user.getId());
        userListener.logUserAction(user, "Viewed all orders");

        logger.info("Customer '{}' retrieved all their orders", user.getUsername());

        return orders.stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get specific order by ID (owned by customer)
     */
    @IsAuthenticated
    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Retrieve an order by its ID for the authenticated customer.")
    public OrderDTO.Output getOrderById(@PathVariable String id, @RequestHeader("Authorization") String token) {
        User user = getCustomerUser(token);

        Order order = orderService.getOrder(id);
        if (order == null)
            throw new NotFoundException("Order not found with ID: " + id);

        if (!order.getUser().getId().equals(user.getId()))
            throw new ForbiddenException("You cannot view another user's order.");

        userListener.logUserAction(user, "Viewed order with ID: " + id);
        logger.info("Customer '{}' viewed order ID: {}", user.getUsername(), id);

        return OrderMapper.toDTO(order);
    }

    /**
     * Get orders by status
     */
    @IsAuthenticated
    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status", description = "Retrieve all orders for the authenticated customer by status.")
    public List<OrderDTO.Output> getOrdersByStatus(
            @PathVariable String status,
            @RequestHeader("Authorization") String token) {

        User user = getCustomerUser(token);

        List<Order> orders = orderService.getOrdersByStatus(status);
        userListener.logUserAction(user, "Viewed orders by status: " + status);

        logger.info("Customer '{}' retrieved orders by status '{}'", user.getUsername(), status);

        return orders.stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create new order
     */
    @IsAuthenticated
    @PostMapping
    @Operation(summary = "Create new order", description = "Place a new order for the authenticated customer.")
    public OrderDTO.Output createOrder(@RequestBody OrderDTO.Input orderDTO, @RequestHeader("Authorization") String token) {
        User user = getCustomerUser(token);

        Order order = OrderMapper.toEntity(orderDTO);
        order.setUser(user); // link the user
        Order createdOrder = orderService.createOrder(order);

        userListener.logUserAction(user, "Created new order");
        logger.info("Customer '{}' created new order with ID: {}", user.getUsername(), createdOrder.getId());

        return OrderMapper.toDTO(createdOrder);
    }

    /**
     * Update an order (only if still pending)
     */
    @IsAuthenticated
    @PutMapping("/{id}")
    @Operation(summary = "Update order", description = "Update an order if it is still pending (authenticated customer only).")
    public OrderDTO.Output updateOrder(
            @PathVariable String id,
            @RequestBody OrderDTO.Input updatedDTO,
            @RequestHeader("Authorization") String token) {

        User user = getCustomerUser(token);
        Order order = orderService.getOrder(id);

        if (order == null)
            throw new NotFoundException("Order not found with ID: " + id);

        if (!order.getUser().getId().equals(user.getId()))
            throw new ForbiddenException("You cannot update another user's order.");

        if (!order.getStatus().equalsIgnoreCase("PENDING"))
            throw new ForbiddenException("Only pending orders can be updated.");

        order.setAddress(updatedDTO.address());
        order.setCity(updatedDTO.city());
        order.setTotalAmount(updatedDTO.totalAmount());

        Order updatedOrder = orderService.updateOrder(id,order);

        userListener.logUserAction(user, "Updated order with ID: " + id);
        logger.info("Customer '{}' updated order ID: {}", user.getUsername(), id);

        return OrderMapper.toDTO(updatedOrder);
    }

    /**
     * Cancel (delete) an order
     */
    @IsAuthenticated
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel order", description = "Cancel an existing order (authenticated customer only).")
    public void deleteOrder(@PathVariable String id, @RequestHeader("Authorization") String token) {
        User user = getCustomerUser(token);

        Order order = orderService.getOrder(id);
        if (order == null)
            throw new NotFoundException("Order not found with ID: " + id);

        if (!order.getUser().getId().equals(user.getId()))
            throw new ForbiddenException("You cannot cancel another user's order.");

        orderService.deleteOrder(id);

        userListener.logUserAction(user, "Cancelled order with ID: " + id);
        logger.info("Customer '{}' cancelled order ID: {}", user.getUsername(), id);
    }

    /**
     * Helper: Extract and validate customer user
     */
    private User getCustomerUser(String token) {
        if (token == null || token.isBlank())
            throw new UnauthorizedException("Missing or invalid authorization token");

        User user = userService.getUserFromToken(token);
        if (user == null)
            throw new UnauthorizedException("Invalid or expired token");

        if (user.getRole() != UserType.CUSTOMER)
            throw new ForbiddenException("Access denied: customer role required");

        return user;
    }
}
