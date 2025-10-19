package com.josephus.e_commerce_backend_app.category.services;

import java.util.List;
import com.josephus.e_commerce_backend_app.category.models.Category;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(String id);
    Category createCategory(Category category);
    Category updateCategory(String id, Category category);
    void deleteCategory(String id);
}
