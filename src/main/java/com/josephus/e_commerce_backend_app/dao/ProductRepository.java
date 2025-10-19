package com.josephus.com.ecommercebackend.dao;

import com.josephus.com.ecommercebackend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryName(String categoryName);
}
