package com.josephus.e_commerce_backend_app.user.mappers;
import com.josephus.e_commerce_backend_app.user.models.User;
import com.josephus.e_commerce_backend_app.user.dtos.UserDTO;

import java.util.stream.Collectors;

public final class UserMapper {
    private UserMapper() {}

    public static User toEntity(UserDTO.Input dto) {
        if (dto == null) return null;
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPasswordHash(dto.passwordHash());
        user.setIsAdmin(dto.isAdmin());
        user.setUserType(dto.userType());
        user.setEnabled(dto.enabled());
        return user;
    }

    public static UserDTO.Output toDTO(User user) {
        if (user == null) return null;
        return new UserDTO.Output(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getIsAdmin(),
                user.getUserType(),
                user.getEnabled(),
                user.getRoles() != null ? user.getRoles().stream().map(r -> r.getId()).collect(Collectors.toSet()) : null,
                user.getCategories() != null ? user.getCategories().stream().map(c -> c.getId()).collect(Collectors.toSet()) : null,
                user.getPaymentMethods() != null ? user.getPaymentMethods().stream().map(p -> p.getId()).collect(Collectors.toSet()) : null
        );
    }
}
