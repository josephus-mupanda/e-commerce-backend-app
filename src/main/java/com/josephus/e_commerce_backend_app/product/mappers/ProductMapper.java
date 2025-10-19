package com.josephus.e_commerce_backend_app.product.mappers;
import com.josephus.e_commerce_backend_app.product.dtos.ProductDTO;
import com.josephus.e_commerce_backend_app.product.models.Product;

public final class ProductMapper {
    private ProductMapper() {}

    public static Product toEntity(ProductDTO.Input dto) {
        if (dto == null) return null;
        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setQuantity(dto.quantity());
        product.setImage(dto.image());
        // categoryId mapping should be done in service layer by fetching Category entity
        return product;
    }

    public static ProductDTO.Output toDTO(Product product) {
        if (product == null) return null;
        return new ProductDTO.Output(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getImage(),
                product.getCategory() != null ? product.getCategory().getId() : null
        );
    }
}

