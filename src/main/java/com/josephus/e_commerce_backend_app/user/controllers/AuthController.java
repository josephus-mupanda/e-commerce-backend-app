package com.josephus.e_commerce_backend_app.user.controllers;

import com.josephus.e_commerce_backend_app.common.annotations.IsAuthenticated;
import com.josephus.e_commerce_backend_app.common.annotations.PublicEndpoint;
import com.josephus.e_commerce_backend_app.common.exceptions.*;
import com.josephus.e_commerce_backend_app.common.listeners.UserListener;
import com.josephus.e_commerce_backend_app.common.models.ConfirmationToken;
import com.josephus.e_commerce_backend_app.common.models.PasswordResetToken;
import com.josephus.e_commerce_backend_app.common.responses.GenericResponse;
import com.josephus.e_commerce_backend_app.common.services.EmailSenderService;
import com.josephus.e_commerce_backend_app.user.dtos.AuthDTO;
import com.josephus.e_commerce_backend_app.user.dtos.UserDTO;
import com.josephus.e_commerce_backend_app.user.mappers.UserMapper;
import com.josephus.e_commerce_backend_app.user.models.User;
import com.josephus.e_commerce_backend_app.user.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration, login, and account management")
public class AuthController {
    private final UserService userService;
    private final UserListener userListener;
    private final UserMapper userMapper;
    private final EmailSenderService emailSenderService;
    @Value("${frontend.base.url}")
    private String url ;
    @Value("${mail.admin.address}")
    private String adminEmail;

    @Autowired
    public AuthController(UserService userService, UserListener userListener, UserMapper userMapper, EmailSenderService emailSenderService) {
        this.userService = userService;
        this.userListener = userListener;
        this.userMapper = userMapper;
        this.emailSenderService = emailSenderService;
    }

    // ==================== REGISTER ====================
    @PublicEndpoint
    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<GenericResponse<UserDTO>> register(
            @Valid @RequestBody AuthDTO.RegisterRequest request
    ) {
        if (userService.hasUserWithUsername(request.username())) {
            throw new ConflictException("Username already exists");
        }
        if (userService.hasUserWithEmail(request.email())) {
            throw new ConflictException("Email already exists");
        }
        if (userService.hasUserWithPhoneNumber(request.phoneNumber())) {
            throw new ConflictException("Phone number already exists");
        }

        User registeredUser = userService.registerUser(
                request.username(),
                request.email(),
                request.password(),
                request.phoneNumber()
        );

        ConfirmationToken token = userService.createConfirmationToken(registeredUser);

        // Send confirmation email
        userService.sendConfirmationEmail(registeredUser, token);

        userListener.logUserAction(registeredUser, "CREATE_USER");

        UserDTO userDTO = userMapper.toDTO(registeredUser);

        return GenericResponse.created("User registered successfully", userDTO);
    }

    // ==================== LOGIN ====================
    @PublicEndpoint
    @Operation(summary = "Login a user and get JWT token")
    @PostMapping("/login")
    public ResponseEntity<GenericResponse<AuthDTO.LoginResponse>> login(
            @Valid @RequestBody AuthDTO.LoginRequest request
    ) {
        String token = userService.verify(request.username(), request.password());

        if ("Failed".equals(token)) {
            throw new UnauthorizedException("Invalid username or password");
        }

        User loggedInUser = userService.getUserByUsername(request.username());

        if (!loggedInUser.isEnabled()) {
            throw new ForbiddenException("Email not confirmed");
        }

        userListener.logUserAction(loggedInUser, "LOGIN");

        AuthDTO.LoginResponse response = new AuthDTO.LoginResponse(token, loggedInUser.getUsername());

        return GenericResponse.ok("Login successful", response);
    }

    // ==================== LOGOUT ====================
    @IsAuthenticated
    @Operation(summary = "Logout a user")
    @PostMapping("/logout")
    public ResponseEntity<GenericResponse<String>> logout(
            @RequestHeader("Authorization") String bearerToken
    ) {
        User user = userService.getUserFromToken(bearerToken);
        if (user == null) {
            throw new BadRequestException("No valid token provided");
        }

        userService.invalidateToken(bearerToken);
        userListener.logUserAction(user, "LOGOUT");

        return GenericResponse.ok("Logged out successfully");
    }

    // ==================== CONFIRM EMAIL ====================
    @PublicEndpoint
    @Operation(summary = "Confirm user email")
    @PostMapping("/confirm")
    public ResponseEntity<GenericResponse<String>> confirmUser(
            @RequestParam("token") String tokenStr
    ) {
        ConfirmationToken token = userService.getConfirmationToken(tokenStr);
        if (token == null || token.getExpiryDate().before(new Date())) {
            throw new BadRequestException("Invalid or expired token");
        }

        User user = token.getUser();
        user.setEnabled(true);
        userService.saveUser(user);
        userService.deleteConfirmationToken(token);

        return GenericResponse.ok("User confirmed successfully");
    }

    // ==================== RESET PASSWORD ====================
    @PublicEndpoint
    @Operation(summary = "Request password reset")
    @PostMapping("/reset-password")
    public ResponseEntity<GenericResponse<String>> resetPassword(
            @RequestBody AuthDTO.ResetPasswordRequest request
    ) {
        User user = userService.getUserByEmail(request.email());
        if (user == null) {
            throw new NotFoundException("Email address not found");
        }

        PasswordResetToken resetToken = userService.createPasswordResetToken(user);
        userService.sendPasswordResetEmail(user, resetToken);

        return GenericResponse.ok("Password reset link sent, check your email");
    }

    // ==================== CHANGE PASSWORD ====================
    @PublicEndpoint
    @Operation(summary = "Change password with token")
    @PostMapping("/change-password")
    public ResponseEntity<GenericResponse<String>> changePassword(
            @RequestParam("token") String tokenStr,
            @RequestBody AuthDTO.ChangePasswordRequest request
    ) {
        PasswordResetToken resetToken = userService.getPasswordResetToken(tokenStr);
        if (resetToken == null || resetToken.getExpiryDate().before(new Date())) {
            throw new BadRequestException("Invalid or expired token");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(userService.encodePassword(request.password()));
        userService.saveUser(user);
        userService.deletePasswordResetToken(resetToken);

        return GenericResponse.ok("Password changed successfully");
    }
    private void sendMail(String email, String subject, String body) {
        emailSenderService.sendEmail(email, subject, body);
    }
    private void sendMailToAdmin(String fromEmail, String subject, String body) {
        sendMail(adminEmail, subject, "Message from: " + fromEmail + "\n\n" + body);
    }
}
