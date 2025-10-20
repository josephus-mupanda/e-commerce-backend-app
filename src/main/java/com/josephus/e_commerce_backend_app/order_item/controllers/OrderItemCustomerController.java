package com.josephus.e_commerce_backend_app.order_item.controllers;

import com.josephus.e_commerce_backend_app.common.annotations.IsAuthenticated;
import com.josephus.e_commerce_backend_app.common.exceptions.NotFoundException;
import com.josephus.e_commerce_backend_app.order.models.Order;
import com.josephus.e_commerce_backend_app.order.services.OrderService;
import com.josephus.e_commerce_backend_app.order_item.dtos.OrderItemDTO;
import com.josephus.e_commerce_backend_app.order_item.mappers.OrderItemMapper;
import com.josephus.e_commerce_backend_app.order_item.models.OrderItem;
import com.josephus.e_commerce_backend_app.order_item.services.OrderItemService;
import com.josephus.e_commerce_backend_app.product.models.Product;
import com.josephus.e_commerce_backend_app.product.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/order-items")
@RequiredArgsConstructor
public class OrderItemCustomerController {

    private static final Logger logger = LoggerFactory.getLogger(OrderItemCustomerController.class);

    private final OrderItemService orderItemService;
    private final ProductService productService;
    private final OrderService orderService;

    // 🔹 Get all items of a specific order
    @IsAuthenticated
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderItemDTO.Output>> getOrderItemsByOrderId(@PathVariable String orderId) {
        logger.info("Fetching order items for order ID: {}", orderId);
        List<OrderItem> items = orderItemService.getOrdersItemsByOrder(orderId);

        if (items.isEmpty()) {
            throw new NotFoundException("No items found for this order.");
        }

        List<OrderItemDTO.Output> outputs = items.stream()
                .map(OrderItemMapper::toDTO)
                .toList();

        return ResponseEntity.ok(outputs);
    }

    // 🔹 Add new order item
    @IsAuthenticated
    @PostMapping("/add/{orderId}")
    public ResponseEntity<OrderItemDTO.Output> addOrderItem(
            @PathVariable String orderId,
            @RequestBody OrderItemDTO.Input input
    ) {
        logger.info("Adding order item for order ID: {}", orderId);

        Product product = productService.getProductById(input.productId());
        if (product == null) {
            throw new NotFoundException("Product with ID " + input.productId() + " not found.");
        }

        Order order = orderService.getOrder(orderId);
        if (order == null) {
            throw new NotFoundException("Order with ID " + orderId + " not found.");
        }

        OrderItem orderItem = OrderItemMapper.toEntity(input);
        orderItem.setProduct(product);
        orderItem.setOrder(order);
        orderItem.setPrice(product.getPrice());

        OrderItem savedItem = orderItemService.createOrderItem(orderItem);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrderItemMapper.toDTO(savedItem));
    }

    // 🔹 Optional: Delete an order item
    @IsAuthenticated
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable String id) {
        logger.info("Deleting order item with ID: {}", id);

        OrderItem orderItem = orderItemService.getOrderItem(id);
        if (orderItem == null) {
            throw new NotFoundException("Order item with ID " + id + " not found.");
        }

        orderItemService.deleteOrderItem(id);
        return ResponseEntity.noContent().build();
    }
}
