package com.josephus.e_commerce_backend_app.common.repositories;
import com.josephus.e_commerce_backend_app.common.models.LogResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<LogResult,Long> {
}
