package com.josephus.e_commerce_backend_app.order.repositories;
import com.josephus.e_commerce_backend_app.order.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    // You can add custom query methods here if needed
    List<Order> findByUserId(String userId);

    List<Order> findByStatus(String status);
}

