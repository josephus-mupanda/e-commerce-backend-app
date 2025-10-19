package com.josephus.e_commerce_backend_app.category.dtos;

import java.util.Set;

public final class CategoryDTO {
    private CategoryDTO() {}

    public record Input(
            String name,
            String description,
            String userId
    ) {}

    public record Output(
            String id,
            String name,
            String description,
            String userId,
            Set<String> productIds
    ) {}
}
