package com.josephus.e_commerce_backend_app.order.models;

import com.josephus.e_commerce_backend_app.common.models.User;
import com.josephus.e_commerce_backend_app.order_item.models.OrderItem;
import com.josephus.e_commerce_backend_app.payment.models.Payment;
import jakarta.persistence.*;
import java.util.UUID;
import com.josephus.e_commerce_backend_app.common.domains.BasicEntity;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
@SQLDelete(sql = "UPDATE orders SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Order extends BasicEntity {

    @Column(nullable = false, unique = true)
    private UUID trackingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String address;
    private String city;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    private double totalAmount;

    @Column(nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment paymentMethod;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<OrderItem> orderItems = new HashSet<>();
}

