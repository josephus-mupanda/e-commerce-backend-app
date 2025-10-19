package com.josephus.com.ecommercebackend.model;
import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name = "Contacts")
@Data
public class ContactRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String clientName;
    private String email;
    private String message;
}
