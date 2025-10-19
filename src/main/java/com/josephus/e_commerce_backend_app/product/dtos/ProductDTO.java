package com.josephus.e_commerce_backend_app.product.dtos;

public final class ProductDTO {
    private ProductDTO() {}

    public record Input(
            String name,
            String description,
            double price,
            int quantity,
            byte[] image,
            String categoryId
    ) {}

    public record Output(
            String id,
            String name,
            String description,
            double price,
            int quantity,
            byte[] image,
            String categoryId
    ) {}
}

