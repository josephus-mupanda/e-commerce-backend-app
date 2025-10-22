package com.josephus.e_commerce_backend_app.common.init;

import com.josephus.e_commerce_backend_app.common.domains.Role;
import com.josephus.e_commerce_backend_app.common.enums.UserType;
import com.josephus.e_commerce_backend_app.common.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Loop through all UserType values and ensure each exists in DB
        for (UserType userType : UserType.values()) {
            if (roleRepository.findByName(userType.name()).isEmpty()) {
                Role role = new Role();
                role.setName(userType.name());
                roleRepository.save(role);
                System.out.println("Created role: " + userType.name());
            }
        }
    }
}
