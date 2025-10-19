package com.josephus.com.ecommercebackend.dao;
import com.josephus.com.ecommercebackend.model.LogResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<LogResult,Long> {
}
