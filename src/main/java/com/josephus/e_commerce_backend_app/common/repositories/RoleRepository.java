package com.josephus.e_commerce_backend_app.common.repositories;
import com.josephus.e_commerce_backend_app.common.domains.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(String name);
}

