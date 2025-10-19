package com.josephus.e_commerce_backend_app.common.mappers;

import com.josephus.e_commerce_backend_app.common.models.PasswordResetToken;
import com.josephus.e_commerce_backend_app.common.dtos.PasswordResetTokenDTO;

public final class PasswordResetTokenMapper {
    private PasswordResetTokenMapper() {}

    public static PasswordResetToken toEntity(PasswordResetTokenDTO.Input dto) {
        if (dto == null) return null;
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(dto.token());
        token.setCreatedDate(dto.createdDate());
        token.setExpiryDate(dto.expiryDate());
        // userId mapping should be done in service layer
        return token;
    }

    public static PasswordResetTokenDTO.Output toDTO(PasswordResetToken token) {
        if (token == null) return null;
        return new PasswordResetTokenDTO.Output(
                token.getId(),
                token.getToken(),
                token.getCreatedDate(),
                token.getExpiryDate(),
                token.getUser() != null ? token.getUser().getId() : null
        );
    }
}

