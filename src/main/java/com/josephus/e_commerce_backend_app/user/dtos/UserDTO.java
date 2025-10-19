package com.josephus.e_commerce_backend_app.user.dtos;
import com.josephus.e_commerce_backend_app.common.enums.UserType;
import java.util.Set;

public final class UserDTO {
    private UserDTO() {}

    public record Input(
            String username,
            String email,
            String passwordHash,
            Boolean isAdmin,
            UserType userType,
            Boolean enabled
    ) {}

    public record Output(
            String id,
            String username,
            String email,
            Boolean isAdmin,
            UserType userType,
            Boolean enabled,
            Set<String> roleIds,
            Set<String> categoryIds,
            Set<String> paymentMethodIds
    ) {}
}
