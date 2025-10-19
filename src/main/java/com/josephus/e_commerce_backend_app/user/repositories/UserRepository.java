package com.josephus.e_commerce_backend_app.user.repositories;
import com.josephus.e_commerce_backend_app.user.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User>  findByUsername (String username);

    Optional<User>  findByEmail (String email);

    Optional<User> findFirstByEmail(String email);

    Optional<User> findFirstByUsername(String username);
}
