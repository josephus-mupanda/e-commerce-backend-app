package com.josephus.e_commerce_backend_app.common.mappers;

import com.josephus.e_commerce_backend_app.common.models.ConfirmationToken;
import com.josephus.e_commerce_backend_app.common.dtos.ConfirmationTokenDTO;

public final class ConfirmationTokenMapper {
    private ConfirmationTokenMapper() {}

    public static ConfirmationToken toEntity(ConfirmationTokenDTO.Input dto) {
        if (dto == null) return null;
        ConfirmationToken token = new ConfirmationToken();
        token.setToken(dto.token());
        token.setCreatedDate(dto.createdDate());
        token.setExpiryDate(dto.expiryDate());
        // userId mapping should be done in service layer
        return token;
    }

    public static ConfirmationTokenDTO.Output toDTO(ConfirmationToken token) {
        if (token == null) return null;
        return new ConfirmationTokenDTO.Output(
                token.getId(),
                token.getToken(),
                token.getCreatedDate(),
                token.getExpiryDate(),
                token.getUser() != null ? token.getUser().getId() : null
        );
    }
}

