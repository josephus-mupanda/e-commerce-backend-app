package com.josephus.e_commerce_backend_app.user.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public final class AuthDTO {

    private AuthDTO() {} // Prevent instantiation

    public record RegisterRequest(
            @NotBlank String username,
            @NotBlank @Email String email,
            @NotBlank String password,
            @NotBlank String phoneNumber
    ) {}

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {}

    public record LoginResponse(
            String accessToken,
            String username
    ) {}

    public record ResetPasswordRequest(
            @NotBlank @Email String email
    ) {}

    public record ChangePasswordRequest(
            @NotBlank String password
    ) {}
}

