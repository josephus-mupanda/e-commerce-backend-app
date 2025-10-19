package com.josephus.e_commerce_backend_app.common.dtos;

public final class ContactRequestDTO {
    private ContactRequestDTO() {}

    public record Input(
            String clientName,
            String email,
            String message
    ) {}

    public record Output(
            String id,
            String clientName,
            String email,
            String message
    ) {}
}

