package com.josephus.com.ecommercebackend.controller.admin;

import com.josephus.e_commerce_backend_app.common.listeners.UserListener;
import com.josephus.com.ecommercebackend.model.Product;
import com.josephus.com.ecommercebackend.model.UserRole;
import com.josephus.com.ecommercebackend.model.Users;
import com.josephus.com.ecommercebackend.service.ProductService;
import com.josephus.com.ecommercebackend.service.UserService;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserListener userListener;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/create")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Long adminId = product.getCategory().getUser().getId();

        logger.info("Admin ID in session: {}", adminId);

        if (adminId == null) {
            // Handle the case where the admin ID is not found in the session
            logger.error("Admin ID not found in session");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Log the retrieved values
        logger.info("Admin ID: {}", adminId);
        logger.info("Product Name: {}", product.getName());
        logger.info("Product Description: {}", product.getDescription());
        logger.info("Product Price: {}", product.getPrice());
        logger.info("Product Quantity: {}", product.getQuantity());
        logger.info("Product Category: {}", product.getCategory());

        Users admin = userService.getUserById(adminId);

        if (admin != null && admin.getRole() == UserRole.ADMIN) {
            try {
                byte[] optimizedImageBytes = optimizeImage(product.getImage());
                product.setImage(optimizedImageBytes);

                Product createdProduct = productService.createProduct(product);

                userListener.logUserAction(admin, "Product created successfully by username:" + admin.getUsername());
                return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
            } catch (IOException e) {
                logger.error("Error optimizing image", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            } catch (IllegalArgumentException e) {
                logger.error("Invalid image format", e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
//
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        logger.info("Updating product with ID: {}", id);

        Long adminId = updatedProduct.getCategory().getUser().getId();
        logger.info("Admin ID in session: {}", adminId);

        if (adminId == null) {
            // Handle the case where the admin ID is not found in the session
            logger.error("Admin ID not found in session");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Product existingProduct = productService.getProductById(id);

        if (existingProduct != null) {
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setPrice(updatedProduct.getPrice());
            existingProduct.setQuantity(updatedProduct.getQuantity());
            existingProduct.setCategory(updatedProduct.getCategory());
            //existingProduct.setImage(updatedProduct.getImage());

            Users admin = userService.getUserById(adminId);

            if (admin != null && admin.getRole() == UserRole.ADMIN) {
                try {
                    byte[] optimizedImageBytes = optimizeImage(updatedProduct.getImage());
                    existingProduct.setImage(optimizedImageBytes);

                    Product savedProduct = productService.updateProduct(id, existingProduct);

                    if (savedProduct != null) {
                        logger.info("Product updated successfully with ID: {}", id);
                        userListener.logUserAction(admin, "Updated product with ID: " + id);
                        return ResponseEntity.ok(savedProduct);
                    } else {
                        logger.error("Failed to update product with ID: {}", id);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                } catch (IOException e) {
                    logger.error("Error optimizing image", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                } catch (IllegalArgumentException e) {
                    logger.error("Invalid image format", e);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            logger.warn("Product with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        logger.info("Deleting product with ID: {}", id);
        Product product = productService.getProductById(id);

        if (product != null) {
            Long adminId = product.getCategory().getUser().getId();
            logger.info("Admin ID in session: {}", adminId);

            if (adminId == null) {
                // Handle the case where the admin ID is not found in the session
                logger.error("Admin ID not found in session");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Users admin = userService.getUserById(adminId);

            if (admin != null && admin.getRole() == UserRole.ADMIN) {
                productService.deleteProduct(id);
                logger.info("Product deleted successfully with ID: {}", id);
                // Log user action using UserListener
                userListener.logUserAction(admin, "Deleted product with ID: " + id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            logger.warn("Product with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    private byte[] optimizeImage(byte[] imageBytes) throws IOException {
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
