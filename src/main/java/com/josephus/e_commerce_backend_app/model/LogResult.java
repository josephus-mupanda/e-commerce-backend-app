package com.josephus.com.ecommercebackend.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
@Entity
@Table(name ="log")
@Data
public class LogResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private LocalDateTime timestamp;

}