package com.josephus.e_commerce_backend_app.category.services;

import java.util.List;
import com.josephus.e_commerce_backend_app.category.models.Category;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(Long id);
    Category createCategory(Category category);
    Category updateCategory(Long id, Category category);
    void deleteCategory(Long id);
}
