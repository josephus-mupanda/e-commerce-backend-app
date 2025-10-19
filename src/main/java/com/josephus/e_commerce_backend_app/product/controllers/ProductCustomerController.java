package com.josephus.e_commerce_backend_app.product.controllers;

import com.josephus.e_commerce_backend_app.common.annotations.PublicEndpoint;
import com.josephus.e_commerce_backend_app.common.exceptions.NotFoundException;
import com.josephus.e_commerce_backend_app.common.listeners.UserListener;
import com.josephus.e_commerce_backend_app.product.dtos.ProductDTO;
import com.josephus.e_commerce_backend_app.product.mappers.ProductMapper;
import com.josephus.e_commerce_backend_app.product.models.Product;
import com.josephus.e_commerce_backend_app.product.services.ProductService;
import com.josephus.e_commerce_backend_app.user.models.User;
import com.josephus.e_commerce_backend_app.user.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customer/products")
@Tag(name = "Customer Products", description = "Endpoints for customers to view available products")
public class ProductCustomerController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserListener userListener;

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve all available products for customers")
    @PublicEndpoint
    public ResponseEntity<List<ProductDTO.Output>> getAllProducts(@RequestHeader("Authorization") String token) {
        User user = userService.getAuthenticatedUser(token);

        List<Product> products = productService.getAllProducts();

        if (products.isEmpty()) {
            throw new NotFoundException("No products available at the moment.");
        }

        userListener.logUserAction(user, "Viewed all available products");

        List<ProductDTO.Output> response = products.stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve detailed information about a specific product by its ID")
    @PublicEndpoint
    public ResponseEntity<ProductDTO.Output> getProductById(
            @RequestHeader("Authorization") String token,
            @PathVariable String id
    ) {
        User user = userService.getAuthenticatedUser(token);

        Product product = productService.getProductById(id);
        if (product == null) {
            throw new NotFoundException("Product with ID " + id + " not found.");
        }

        userListener.logUserAction(user, "Viewed product details for ID: " + id);

        return ResponseEntity.ok(ProductMapper.toDTO(product));
    }

    @GetMapping("/category/{name}")
    @Operation(summary = "Get products by category", description = "Retrieve all products belonging to a specific category by name")
    @PublicEndpoint
    public ResponseEntity<List<ProductDTO.Output>> getProductByCategoryName(
            @RequestHeader("Authorization") String token,
            @PathVariable String name
    ) {
        User user = userService.getAuthenticatedUser(token);

        List<Product> products = productService.getProductsByCategoryName(name);

        if (products == null || products.isEmpty()) {
            throw new NotFoundException("No products found for category: " + name);
        }

        userListener.logUserAction(user, "Viewed products in category: " + name);

        List<ProductDTO.Output> response = products.stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}

