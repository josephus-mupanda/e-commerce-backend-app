package com.josephus.e_commerce_backend_app.common.services.impl;
import com.josephus.e_commerce_backend_app.common.domains.Role;
import com.josephus.e_commerce_backend_app.common.enums.UserType;
import com.josephus.e_commerce_backend_app.common.repositories.RoleRepository;
import com.josephus.e_commerce_backend_app.user.models.User;
import com.josephus.e_commerce_backend_app.user.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AdminAccountInitializer implements CommandLineRunner {
    private final UserService userService;
    private final RoleRepository roleRepository;

    @Value("${application.users.admin.email}")
    private String adminEmail;

    @Value("${application.users.admin.password}")
    private String adminPassword;

    public AdminAccountInitializer(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    private static final Logger logger = LoggerFactory.getLogger(AdminAccountInitializer.class);
    @Override
    public void run(String... args) throws Exception {

        if (!userService.hasUserWithEmail(adminEmail)) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail(adminEmail);
            admin.setPasswordHash(userService.encodePassword(adminPassword));
            admin.setEnabled(true);

            // Assign ADMIN role
            Role adminRole = roleRepository.findByName(UserType.ADMIN.name())
                    .orElseThrow(() -> new RuntimeException("Role ADMIN not found"));
            admin.setRoles(Set.of(adminRole));


            userService.saveUser(admin);

            logger.info("Admin account created: " + adminEmail);
        } else {
            logger.info("Admin account already exists: " + adminEmail);
        }
    }
}
