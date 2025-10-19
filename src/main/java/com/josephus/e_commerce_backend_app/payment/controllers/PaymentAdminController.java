package com.josephus.e_commerce_backend_app.payment.controllers;

import com.josephus.e_commerce_backend_app.common.enums.UserType;
import com.josephus.e_commerce_backend_app.common.listeners.UserListener;
import com.josephus.e_commerce_backend_app.payment.dtos.PaymentDTO;
import com.josephus.e_commerce_backend_app.payment.mappers.PaymentMapper;
import com.josephus.e_commerce_backend_app.payment.models.Payment;
import com.josephus.e_commerce_backend_app.payment.services.PaymentService;
import com.josephus.e_commerce_backend_app.user.models.User;
import com.josephus.e_commerce_backend_app.common.annotations.IsAuthenticated;
import com.josephus.e_commerce_backend_app.common.responses.GenericResponse;
import com.josephus.e_commerce_backend_app.common.exceptions.ForbiddenException;
import com.josephus.e_commerce_backend_app.common.exceptions.NotFoundException;
import com.josephus.e_commerce_backend_app.user.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/payments")
@Tag(name = "Admin Payments", description = "Admin endpoints to manage payments")
@IsAuthenticated
public class PaymentAdminController {

    private final PaymentService paymentService;
    private final UserService userService;
    private final UserListener userListener;

    @Autowired
    public PaymentAdminController(PaymentService paymentService, UserService userService, UserListener userListener) {
        this.paymentService = paymentService;
        this.userService = userService;
        this.userListener = userListener;
    }

    // ==================== GET ALL PAYMENTS ====================
    @Operation(summary = "Get all payments")
    @GetMapping
    public GenericResponse<List<PaymentDTO.Output>> getAllPayments() {
        List<PaymentDTO.Output> dtos = paymentService.getAllPayments()
                .stream()
                .map(PaymentMapper::toDTO)
                .collect(Collectors.toList());
        return new GenericResponse<>("Payments retrieved successfully", dtos);
    }

    // ==================== GET PAYMENT BY ID ====================
    @Operation(summary = "Get payment by ID")
    @GetMapping("/{id}")
    public GenericResponse<PaymentDTO.Output> getPaymentById(@PathVariable String id) {
        Payment payment = paymentService.getPaymentById(id);
        if (payment == null) throw new NotFoundException("Payment not found");

        return new GenericResponse<>("Payment retrieved successfully", PaymentMapper.toDTO(payment));
    }

    // ==================== CREATE PAYMENT ====================
    @Operation(summary = "Create a new payment")
    @PostMapping
    public GenericResponse<PaymentDTO.Output> createPayment(
            @RequestBody PaymentDTO.Input paymentDTO,
            @RequestHeader("X-Admin-Id") String adminId
    ) {
        User admin = validateAdmin(adminId);

        Payment payment = PaymentMapper.toEntity(paymentDTO);
        Payment created = paymentService.createPayment(payment);

        userListener.logUserAction(admin, "Created payment with ID: " + created.getId());
        return new GenericResponse<>("Payment created successfully", PaymentMapper.toDTO(created));
    }

    // ==================== UPDATE PAYMENT ====================
    @Operation(summary = "Update an existing payment")
    @PutMapping("/{id}")
    public GenericResponse<PaymentDTO.Output> updatePayment(
            @PathVariable String id,
            @RequestBody PaymentDTO.Input paymentDTO,
            @RequestHeader("X-Admin-Id") String adminId
    ) {
        User admin = validateAdmin(adminId);

        Payment existing = paymentService.getPaymentById(id);
        if (existing == null) throw new NotFoundException("Payment not found");

        PaymentMapper.updateEntity(existing, paymentDTO);

        Payment updated = paymentService.updatePayment(id, existing);
        userListener.logUserAction(admin, "Updated payment with ID: " + updated.getId());

        return new GenericResponse<>("Payment updated successfully", PaymentMapper.toDTO(updated));
    }

    // ==================== DELETE PAYMENT ====================
    @Operation(summary = "Delete a payment")
    @DeleteMapping("/{id}")
    public GenericResponse<Void> deletePayment(
            @PathVariable String id,
            @RequestHeader("X-Admin-Id") String adminId
    ) {
        User admin = validateAdmin(adminId);

        Payment existing = paymentService.getPaymentById(id);
        if (existing == null) throw new NotFoundException("Payment not found");

        paymentService.deletePayment(id);
        userListener.logUserAction(admin, "Deleted payment with ID: " + id);

        return new GenericResponse<>("Payment deleted successfully", null);
    }

    // ==================== HELPERS ====================
    private User validateAdmin(String adminId) {
        User admin = userService.getUserById(adminId);
        if (admin == null || admin.getRole() != UserType.ADMIN) {
            throw new ForbiddenException("Access denied");
        }
        return admin;
    }
}
