package com.josephus.com.ecommercebackend.controller.authenticate;
import com.josephus.e_commerce_backend_app.common.listeners.UserListener;
import com.josephus.com.ecommercebackend.model.ConfirmationToken;
import com.josephus.com.ecommercebackend.model.ContactRequest;
import com.josephus.com.ecommercebackend.model.PasswordResetToken;
import com.josephus.com.ecommercebackend.model.Users;
import com.josephus.com.ecommercebackend.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserListener userListener;

    @Autowired
    private ECommerceBackendApplication application;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${frontend.base.url}")
    private String frontendBaseUrl;
    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {
        try {
            List<Users> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error fetching all users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Users user) {

        if(userService.hasUserWithEmail(user.getEmail())){
            return  new ResponseEntity<>("User email already exists", HttpStatus.NOT_ACCEPTABLE);
        }
        if(userService.hasUserWithUsername(user.getUsername())){
            return  new ResponseEntity<>("Username already exists", HttpStatus.NOT_ACCEPTABLE);
        }

        logger.info("User registration request received for username: {}", user.getUsername());

        Users newUser = userService.registerUser(user.getUsername(), user.getEmail(), user.getPassword());

        if (newUser != null) {
            // Generate confirmation token
            ConfirmationToken confirmationToken = userService.createConfirmationToken(newUser);

            // Send confirmation email
            String confirmationUrl = frontendBaseUrl+"/api/users/confirm?token=" + confirmationToken.getToken();

            application.sendMail(newUser.getEmail(), "Confirm your email", "Click the link to confirm your email: " + confirmationUrl);

            // Send email to admin
            application.sendMailToAdmin(newUser.getEmail(), "User registered successfully", "With username:  " + newUser.getUsername() + "\nEmail: " + newUser.getEmail());

            logger.info("User registered successfully with username: {}", newUser.getUsername());

            userListener.logUserAction(newUser,"User registered successfully with username:"+newUser.getUsername());

            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } else {
            // Handle registration failure here
            logger.error("User registration failed for username: {}", user.getUsername());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    //==============================CONFIRM ACCOUNT ================================
    @GetMapping("/confirm")
    public ResponseEntity<?> confirmUserAccount(@RequestParam("token") String confirmationToken) {
        ConfirmationToken token = userService.getConfirmationToken(confirmationToken);

        Map<String, String> response = new HashMap<>();

        if (token != null) {
            if (token.getExpiryDate().before(new Date())) {
                response.put("message", "Token has expired");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            Users user = token.getUser();
            user.setEnabled(true);  // Enable the user
            userService.saveUser(user);
            userService.deleteConfirmationToken(token);  // Remove the used token

            response.put("message", "Email confirmed successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("message", "Invalid or expired token");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    //============================== FORGET PASSWORD  ================================

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            logger.error("Email address is required");
            return new ResponseEntity<>("Email address is required", HttpStatus.BAD_REQUEST);
        }

        Users user = userService.getUserByEmail(email);
        if (user == null) {
            logger.error("Email address not found: {}", email);
            return new ResponseEntity<>("Email address not found", HttpStatus.NOT_FOUND);
        }

        PasswordResetToken token = userService.createPasswordResetToken(user);
        String resetUrl = frontendBaseUrl + "/reset-password?token=" + token.getToken();

        logger.info("Generated password reset token for user: {}", user.getEmail());
        application.sendMail(user.getEmail(), "Reset your password", "Click the link to reset your password: " + resetUrl);

        logger.info("Password reset link sent to email: {}", email);
        return new ResponseEntity<>("Password reset link sent to email", HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {

        String token = request.get("token");
        String newPassword = request.get("newPassword");

        logger.info("Received password reset request with token: {}", token);

        if (token == null || token.isEmpty()) {
            logger.error("Token is required");
            return new ResponseEntity<>("Token is required", HttpStatus.BAD_REQUEST);
        }

        if (newPassword == null || newPassword.isEmpty()) {
            logger.error("New password is required");
            return new ResponseEntity<>("New password is required", HttpStatus.BAD_REQUEST);
        }

        PasswordResetToken resetToken = userService.getPasswordResetToken(token);
        if (resetToken == null || resetToken.getExpiryDate().before(new Date())) {
            logger.error("Invalid or expired token: {}", token);
            return new ResponseEntity<>("Invalid or expired token", HttpStatus.BAD_REQUEST);
        }

        Users user = resetToken.getUser();
        user.setPassword(userService.encodePassword(newPassword));
        userService.saveUser(user);
        userService.deletePasswordResetToken(resetToken);

        logger.info("Password reset successfully for user: {}", user.getEmail());
        return new ResponseEntity<>("Password reset successfully", HttpStatus.OK);
    }

    //============================== CONTACT  ================================

    @PostMapping("/contact")
    public ResponseEntity<?> contactUs(@RequestBody ContactRequest contactRequest) {

        String userEmail = contactRequest.getEmail();
        String userName = contactRequest.getClientName();
        String userMessage = contactRequest.getMessage();

        // Send email to user
        application.sendMail(userEmail, "Thank you for contacting us!", "We have received your message and will get back to you shortly.");

        // Send email to admin
        application.sendMailToAdmin(userEmail, "New contact form submission", "Name: " + userName + "\nEmail: " + userEmail + "\nMessage: " + userMessage);

        return new ResponseEntity<>("Email sent successfully", HttpStatus.CREATED);
    }

    //============================== LOGIN  ================================

    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody Users user, HttpSession session, HttpServletResponse response) {
        String email = user.getEmail();
        String password = user.getPassword();

        Users loggedInUser = userService.loginUser(email, password);

        if (loggedInUser != null) {

            if (!loggedInUser.isEnabled()) {
                return new ResponseEntity<>("Email not confirmed", HttpStatus.FORBIDDEN);
            }

            session.setAttribute("loggedInUserId", loggedInUser.getId());
            session.setAttribute("userRole", loggedInUser.getRole().toString());
            session.setAttribute("username", loggedInUser.getUsername());

            Map<String, String> responseData = new HashMap<>();
            responseData.put("sessionId", loggedInUser.getId().toString());
            responseData.put("userRole", loggedInUser.getRole().toString());
            responseData.put("username", loggedInUser.getUsername());


            logger.info("User logged in successfully with Id: {}", loggedInUser.getId());

            userListener.logUserAction(loggedInUser, "User logged in successfully with username:" + loggedInUser.getUsername());

            return ResponseEntity.ok(responseData);

        } else {
            logger.warn("Invalid login attempt for email: {}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

    //============================== LOGOUT  ================================

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody Users user,HttpSession session, HttpServletResponse response) {
        // Logout logic
        Long userId = user.getId();
        String userRole = user.getRole().toString();
        if (userId != null && userRole != null) {
            Users users = userService.getUserById(userId);
            if (users != null) {
                logger.info("User logged out: {}", userId);
                // Log user action using UserListener
                userListener.logUserAction(users, "User logged out successfully");
            }
        }
        if (session != null) {
            session.invalidate();
            logger.info("User logged out successfully");
            return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
        } else {
            logger.warn("No active session found for logout");
            return new ResponseEntity<>("No user logged in", HttpStatus.UNAUTHORIZED);
        }
    }
}
