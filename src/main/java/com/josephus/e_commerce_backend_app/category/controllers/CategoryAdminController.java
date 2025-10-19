package com.josephus.e_commerce_backend_app.category.controllers;

import com.josephus.e_commerce_backend_app.category.dtos.CategoryDTO;
import com.josephus.e_commerce_backend_app.category.mappers.CategoryMapper;
import com.josephus.e_commerce_backend_app.category.models.Category;
import com.josephus.e_commerce_backend_app.category.services.CategoryService;
import com.josephus.e_commerce_backend_app.common.annotations.IsAuthenticated;
import com.josephus.e_commerce_backend_app.common.annotations.PublicEndpoint;
import com.josephus.e_commerce_backend_app.common.enums.UserType;
import com.josephus.e_commerce_backend_app.common.exceptions.ForbiddenException;
import com.josephus.e_commerce_backend_app.common.exceptions.NotFoundException;
import com.josephus.e_commerce_backend_app.common.listeners.UserListener;
import com.josephus.e_commerce_backend_app.common.responses.GenericResponse;
import com.josephus.e_commerce_backend_app.user.models.User;
import com.josephus.e_commerce_backend_app.user.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/categories")
@Tag(name = "Admin Categories", description = "Admin endpoints to manage categories")
@IsAuthenticated // All endpoints require authentication
public class CategoryAdminController {

    private final CategoryService categoryService;
    private final UserService userService;
    private final UserListener userListener;

    @Autowired
    public CategoryAdminController(
            CategoryService categoryService,
            UserService userService,
            UserListener userListener
    ) {
        this.categoryService = categoryService;
        this.userService = userService;
        this.userListener = userListener;
    }

    // ==================== GET ALL ====================
    @Operation(summary = "Get all categories")
    @PublicEndpoint
    @GetMapping
    public GenericResponse<List<CategoryDTO.Output>> getAllCategories() {
        List<CategoryDTO.Output> dtos = categoryService.getAllCategories().stream()
                .map(CategoryMapper::toDTO)
                .collect(Collectors.toList());

        return new GenericResponse<>("Categories retrieved successfully", dtos);
    }

    // ==================== GET BY ID ====================
    @Operation(summary = "Get category by ID")
    @PublicEndpoint
    @GetMapping("/{id}")
    public GenericResponse<CategoryDTO.Output> getCategoryById(@PathVariable String id) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) throw new NotFoundException("Category not found");

        return new GenericResponse<>("Category retrieved successfully", CategoryMapper.toDTO(category));
    }

    // ==================== CREATE ====================
    @Operation(summary = "Create a new category")
    @PostMapping
    public GenericResponse<CategoryDTO.Output> createCategory(
            @RequestBody CategoryDTO.Input categoryDTO,
            @RequestHeader("X-Admin-Id") String adminId
    ) {
        User admin = userService.getUserById(adminId);
        if (admin == null || admin.getRole() != UserType.ADMIN) throw new ForbiddenException("Access denied");

        Category category = CategoryMapper.toEntity(categoryDTO);
        Category created = categoryService.createCategory(category);

        userListener.logUserAction(admin, "Created category with ID: " + created.getId());

        return new GenericResponse<>("Category created successfully", CategoryMapper.toDTO(created));
    }

    // ==================== UPDATE ====================
    @Operation(summary = "Update an existing category")
    @PutMapping("/{id}")
    public GenericResponse<CategoryDTO.Output> updateCategory(
            @PathVariable String id,
            @RequestBody CategoryDTO.Input categoryDTO,
            @RequestHeader("X-Admin-Id") String adminId
    ) {
        User admin = userService.getUserById(adminId);
        if (admin == null || admin.getRole() != UserType.ADMIN) throw new ForbiddenException("Access denied");

        Category existing = categoryService.getCategoryById(id);
        if (existing == null) throw new NotFoundException("Category not found");

        existing.setName(categoryDTO.name());
        existing.setDescription(categoryDTO.description());

        Category updated = categoryService.updateCategory(id, existing);
        userListener.logUserAction(admin, "Updated category with ID: " + updated.getId());

        return new GenericResponse<>("Category updated successfully", CategoryMapper.toDTO(updated));
    }

    // ==================== DELETE ====================
    @Operation(summary = "Delete a category")
    @DeleteMapping("/{id}")
    public GenericResponse<Void> deleteCategory(
            @PathVariable String id,
            @RequestHeader("X-Admin-Id") String adminId
    ) {
        User admin = userService.getUserById(adminId);
        if (admin == null || admin.getRole() != UserType.ADMIN) throw new ForbiddenException("Access denied");

        Category existing = categoryService.getCategoryById(id);
        if (existing == null) throw new NotFoundException("Category not found");

        categoryService.deleteCategory(id);
        userListener.logUserAction(admin, "Deleted category with ID: " + id);

        return new GenericResponse<>("Category deleted successfully", null);
    }
}

