package com.josephus.e_commerce_backend_app.payment.controllers;

import com.josephus.e_commerce_backend_app.common.annotations.IsAuthenticated;
import com.josephus.e_commerce_backend_app.common.annotations.PublicEndpoint;
import com.josephus.e_commerce_backend_app.common.exceptions.NotFoundException;
import com.josephus.e_commerce_backend_app.common.listeners.UserListener;
import com.josephus.e_commerce_backend_app.payment.dtos.PaymentDTO;
import com.josephus.e_commerce_backend_app.payment.mappers.PaymentMapper;
import com.josephus.e_commerce_backend_app.payment.models.Payment;
import com.josephus.e_commerce_backend_app.payment.services.PaymentService;
import com.josephus.e_commerce_backend_app.user.models.User;
import com.josephus.e_commerce_backend_app.user.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customer/payments")
@Tag(name = "Customer Payments", description = "Endpoints for customers to view their payments")
public class PaymentCustomerController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserListener userListener;

    @GetMapping
    @Operation(summary = "Get all payments", description = "Retrieve a list of all payments made by the authenticated customer")
    @PublicEndpoint
    public ResponseEntity<List<PaymentDTO.Output>> getAllPayments(@RequestHeader("Authorization") String token) {
        User user = userService.getUserFromToken(token);
        List<Payment> payments = paymentService.getAllPayments();

        if (payments.isEmpty()) {
            throw new NotFoundException("No payments found for this customer.");
        }

        userListener.logUserAction(user, "Viewed payment list");

        List<PaymentDTO.Output> response = payments.stream()
                .map(PaymentMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieve payment details by its ID for the authenticated customer")
    @PublicEndpoint
    public ResponseEntity<PaymentDTO.Output> getPaymentById(
            @RequestHeader("Authorization") String token,
            @PathVariable String id
    ) {
        User user = userService.getUserFromToken(token);
        Payment payment = paymentService.getPaymentById(id);

        if (payment == null || !payment.getUser().getId().equals(user.getId())) {
            throw new NotFoundException("Payment not found or not accessible.");
        }

        userListener.logUserAction(user, "Viewed payment details for ID: " + id);

        return ResponseEntity.ok(PaymentMapper.toDTO(payment));
    }
}
