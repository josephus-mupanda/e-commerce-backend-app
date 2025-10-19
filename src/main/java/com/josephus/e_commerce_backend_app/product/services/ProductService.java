package com.josephus.e_commerce_backend_app.product.services;
import com.josephus.e_commerce_backend_app.product.models.Product;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(String id);
    Product createProduct(Product product);
    Product updateProduct(String id, Product product);
    void deleteProduct(String id);
    List<Product> getProductsByCategoryName(String categoryName);
}
