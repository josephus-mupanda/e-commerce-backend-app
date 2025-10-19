package com.josephus.e_commerce_backend_app.common.services.impl;
import com.josephus.com.ecommercebackend.model.UserRole;
import com.josephus.e_commerce_backend_app.model.Users;
import com.josephus.e_commerce_backend_app.user.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminAccountInitializer implements CommandLineRunner {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${default.admin.username}")
    private String adminUsername;

    @Value("${default.admin.email}")
    private String adminEmail;

    @Value("${default.admin.password}")
    private String adminPassword;

    private static final Logger logger = LoggerFactory.getLogger(AdminAccountInitializer.class);
    @Override
    public void run(String... args) throws Exception {

        if (!userService.hasUserWithEmail(adminEmail)) {
            Users admin = new Users();
            admin.setUsername(adminUsername);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(UserRole.ADMIN);
            admin.setEnabled(true);  // No need for email confirmation

            userService.saveUser(admin);

            logger.info("Admin account created: " + adminEmail);
        } else {
            logger.info("Admin account already exists: " + adminEmail);
        }
    }
}
