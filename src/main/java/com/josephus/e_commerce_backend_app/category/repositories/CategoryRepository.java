package com.josephus.e_commerce_backend_app.category.repositories;

import com.josephus.e_commerce_backend_app.category.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    // You can add custom query methods if needed
}
