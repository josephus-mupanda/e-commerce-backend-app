package com.josephus.e_commerce_backend_app.category.mappers;
import com.josephus.e_commerce_backend_app.category.dtos.CategoryDTO;
import com.josephus.e_commerce_backend_app.category.models.Category;
import java.util.stream.Collectors;

public final class CategoryMapper {
    private CategoryMapper() {}

    public static Category toEntity(CategoryDTO.Input dto) {
        if (dto == null) return null;
        Category category = new Category();
        category.setName(dto.name());
        category.setDescription(dto.description());
        // userId mapping should be done in service layer by fetching User entity
        return category;
    }

    public static CategoryDTO.Output toDTO(Category category) {
        if (category == null) return null;
        return new CategoryDTO.Output(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getUser() != null ? category.getUser().getId() : null,
                category.getProducts() != null ? category.getProducts().stream().map(p -> p.getId()).collect(Collectors.toSet()) : null
        );
    }
}

