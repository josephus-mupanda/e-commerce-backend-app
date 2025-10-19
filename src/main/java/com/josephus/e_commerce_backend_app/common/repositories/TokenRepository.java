package com.josephus.e_commerce_backend_app.common.repositories;
import com.josephus.e_commerce_backend_app.common.domains.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {
    boolean existsByToken(String token);
}
