package com.josephus.com.ecommercebackend.service;
import com.josephus.com.ecommercebackend.model.Product;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product createProduct(Product product);
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
    List<Product> getProductsByCategoryName(String categoryName);
}
