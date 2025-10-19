package com.josephus.e_commerce_backend_app.product.services;
import com.josephus.e_commerce_backend_app.product.models.Product;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product createProduct(Product product);
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
    List<Product> getProductsByCategoryName(String categoryName);
}
