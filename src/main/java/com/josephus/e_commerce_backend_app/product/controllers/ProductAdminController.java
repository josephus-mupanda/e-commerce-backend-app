package com.josephus.e_commerce_backend_app.product.controllers;

import com.josephus.e_commerce_backend_app.common.enums.UserType;
import com.josephus.e_commerce_backend_app.common.listeners.UserListener;
import com.josephus.e_commerce_backend_app.product.dtos.ProductDTO;
import com.josephus.e_commerce_backend_app.product.mappers.ProductMapper;
import com.josephus.e_commerce_backend_app.product.models.Product;
import com.josephus.e_commerce_backend_app.product.services.ProductService;
import com.josephus.e_commerce_backend_app.user.models.User;
import com.josephus.e_commerce_backend_app.common.annotations.IsAuthenticated;
import com.josephus.e_commerce_backend_app.common.responses.GenericResponse;
import com.josephus.e_commerce_backend_app.common.exceptions.ForbiddenException;
import com.josephus.e_commerce_backend_app.common.exceptions.NotFoundException;
import com.josephus.e_commerce_backend_app.user.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/products")
@Tag(name = "Admin Products", description = "Admin endpoints to manage products")
@IsAuthenticated
public class ProductAdminController {

    private final ProductService productService;
    private final UserService userService;
    private final UserListener userListener;

    @Autowired
    public ProductAdminController(ProductService productService, UserService userService, UserListener userListener) {
        this.productService = productService;
        this.userService = userService;
        this.userListener = userListener;
    }

    // ==================== GET ALL PRODUCTS ====================
    @Operation(summary = "Get all products")
    @GetMapping
    public GenericResponse<List<ProductDTO.Output>> getAllProducts() {
        List<ProductDTO.Output> dtos = productService.getAllProducts().stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
        return new GenericResponse<>("Products retrieved successfully", dtos);
    }

    // ==================== GET PRODUCT BY ID ====================
    @Operation(summary = "Get product by ID")
    @GetMapping("/{id}")
    public GenericResponse<ProductDTO.Output> getProductById(@PathVariable String id) {
        Product product = productService.getProductById(id);
        if (product == null) throw new NotFoundException("Product not found");

        return new GenericResponse<>("Product retrieved successfully", ProductMapper.toDTO(product));
    }

    // ==================== CREATE PRODUCT ====================
    @Operation(summary = "Create a new product")
    @PostMapping
    public GenericResponse<ProductDTO.Output> createProduct(
            @RequestBody ProductDTO.Input productDTO,
            @RequestHeader("Authorization") String token
    ) {
        User admin = validateAdmin(token);

        Product product = ProductMapper.toEntity(productDTO);
        try {
            product.setImage(optimizeImage(productDTO.image()));
        } catch (IOException e) {
            throw new RuntimeException("Error optimizing image");
        }

        Product created = productService.createProduct(product);
        userListener.logUserAction(admin, "Created product with ID: " + created.getId());

        return new GenericResponse<>("Product created successfully", ProductMapper.toDTO(created));
    }


    // ==================== UPDATE PRODUCT ====================
    @Operation(summary = "Update an existing product")
    @PutMapping("/{id}")
    public GenericResponse<ProductDTO.Output> updateProduct(
            @PathVariable String id,
            @RequestBody ProductDTO.Input productDTO,
            @RequestHeader("Authorization") String token
    ) {
        User admin = validateAdmin(token);

        Product existing = productService.getProductById(id);
        if (existing == null) throw new NotFoundException("Product not found");

        ProductMapper.updateEntity(existing, productDTO);

        try {
            existing.setImage(optimizeImage(productDTO.image()));
        } catch (IOException e) {
            throw new RuntimeException("Error optimizing image");
        }

        Product updated = productService.updateProduct(id, existing);
        userListener.logUserAction(admin, "Updated product with ID: " + updated.getId());

        return new GenericResponse<>("Product updated successfully", ProductMapper.toDTO(updated));
    }

    // ==================== DELETE PRODUCT ====================
    @Operation(summary = "Delete a product")
    @DeleteMapping("/{id}")
    public GenericResponse<Void> deleteProduct(
            @PathVariable String id,
            @RequestHeader("Authorization") String token
    ) {
        User admin = validateAdmin(token);

        Product existing = productService.getProductById(id);
        if (existing == null) throw new NotFoundException("Product not found");

        productService.deleteProduct(id);
        userListener.logUserAction(admin, "Deleted product with ID: " + id);

        return new GenericResponse<>("Product deleted successfully", null);
    }

    // ==================== HELPERS ====================
    private User validateAdmin(String token) {
        User admin = userService.getUserFromToken(token);
        if (admin == null || admin.getRoles().stream().noneMatch(role -> {
            try {
                return UserType.valueOf(role.getName().toUpperCase()) == UserType.ADMIN;
            } catch (IllegalArgumentException e) {
                return false;
            }
        })) {
            throw new ForbiddenException("Access denied: Admins only");
        }
        return admin;
    }

    private byte[] optimizeImage(byte[] imageBytes) throws IOException {
        if (imageBytes == null || imageBytes.length == 0) return null;

        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(inputStream)
                .size(800, 800)
                .outputFormat("jpg")
                .outputQuality(0.8)
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }
}

