package com.josephus.e_commerce_backend_app.common.dtos;
import java.time.LocalDateTime;

public final class LogResultDTO {
    private LogResultDTO() {}

    public record Input(
            String action,
            String username,
            LocalDateTime timestamp
    ) {}

    public record Output(
            String id,
            String action,
            String username,
            LocalDateTime timestamp
    ) {}
}
