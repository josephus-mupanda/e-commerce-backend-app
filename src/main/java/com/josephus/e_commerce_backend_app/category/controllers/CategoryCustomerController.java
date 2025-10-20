package com.josephus.e_commerce_backend_app.category.controllers;

import com.josephus.e_commerce_backend_app.category.dtos.CategoryDTO;
import com.josephus.e_commerce_backend_app.category.mappers.CategoryMapper;
import com.josephus.e_commerce_backend_app.category.models.Category;
import com.josephus.e_commerce_backend_app.category.services.CategoryService;
import com.josephus.e_commerce_backend_app.common.annotations.PublicEndpoint;
import com.josephus.e_commerce_backend_app.common.exceptions.NotFoundException;
import com.josephus.e_commerce_backend_app.common.responses.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customer/categories")
@Tag(name = "Categories", description = "Customer endpoints to access product categories")
public class CategoryCustomerController {
    private final CategoryService categoryService;
    @Autowired
    public CategoryCustomerController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // ==================== GET ALL CATEGORIES ====================
    @PublicEndpoint
    @Operation(summary = "Get all product categories")
    @GetMapping
    public GenericResponse<List<CategoryDTO.Output>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryDTO.Output> dtos = categories.stream()
                .map(CategoryMapper::toDTO)
                .collect(Collectors.toList());

        return new GenericResponse<>("Categories retrieved successfully", dtos);
    }

    // ==================== GET CATEGORY BY ID ====================
    @PublicEndpoint
    @Operation(summary = "Get category by ID")
    @GetMapping("/{id}")
    public GenericResponse<CategoryDTO.Output> getCategoryById(@PathVariable String id) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            throw new NotFoundException("Category not found");
        }
        CategoryDTO.Output dto = CategoryMapper.toDTO(category);
        return new GenericResponse<>("Category retrieved successfully", dto);
    }
}
