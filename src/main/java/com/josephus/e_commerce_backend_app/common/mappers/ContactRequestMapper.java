package com.josephus.e_commerce_backend_app.common.mappers;

import com.josephus.e_commerce_backend_app.common.models.ContactRequest;
import com.josephus.e_commerce_backend_app.common.dtos.ContactRequestDTO;

public final class ContactRequestMapper {
    private ContactRequestMapper() {}

    public static ContactRequest toEntity(ContactRequestDTO.Input dto) {
        if (dto == null) return null;
        ContactRequest contact = new ContactRequest();
        contact.setClientName(dto.clientName());
        contact.setEmail(dto.email());
        contact.setMessage(dto.message());
        return contact;
    }

    public static ContactRequestDTO.Output toDTO(ContactRequest contact) {
        if (contact == null) return null;
        return new ContactRequestDTO.Output(
                contact.getId(),
                contact.getClientName(),
                contact.getEmail(),
                contact.getMessage()
        );
    }
}

