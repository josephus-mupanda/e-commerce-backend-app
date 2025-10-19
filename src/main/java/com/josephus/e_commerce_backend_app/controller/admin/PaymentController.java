package com.josephus.com.ecommercebackend.controller.admin;

import com.josephus.com.ecommercebackend.listeners.UserListener;
import com.josephus.com.ecommercebackend.model.Payment;
import com.josephus.com.ecommercebackend.model.UserRole;
import com.josephus.com.ecommercebackend.model.Users;
import com.josephus.com.ecommercebackend.service.PaymentService;
import com.josephus.com.ecommercebackend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserListener userListener;

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);
        if (payment != null) {
            return ResponseEntity.ok(payment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {

        Long adminId = payment.getUser().getId();

        logger.info("Admin ID in session: {}", adminId);

        if (adminId == null) {
            // Handle the case where the admin ID is not found in the session
            logger.error("Admin ID not found in session");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Log the retrieved values
        logger.info("Admin ID: {}", adminId);
        logger.info("Payment Method: {}", payment.getPaymentMethod());

        Users admin = userService.getUserById(adminId);

        if (admin != null && admin.getRole() == UserRole.ADMIN) {

            Payment createdPayment = paymentService.createPayment(payment);

            userListener.logUserAction(admin, "Payment created successfully by username:" + admin.getUsername());

            return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Payment> updatePayment(@PathVariable Long id, @RequestBody Payment updatedPayment) {
        logger.info("Updating payment with ID: {}", id);

        Long adminId = updatedPayment.getUser().getId();

        logger.info("Admin ID in session: {}", adminId);

        if (adminId == null) {
            // Handle the case where the admin ID is not found in the session
            logger.error("Admin ID not found in session");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Payment existingPayment = paymentService.getPaymentById(id);

        if (existingPayment != null) {

            existingPayment.setPaymentMethod(updatedPayment.getPaymentMethod());

            Users admin = userService.getUserById(adminId);

            if (admin != null && admin.getRole() == UserRole.ADMIN) {

                Payment savedPayment = paymentService.updatePayment(id, existingPayment);

                if (savedPayment != null) {
                    logger.info("Payment updated successfully with ID: {}", id);

                    // Log user action using UserListener
                    userListener.logUserAction(admin, "Updated payment with ID: " + id);

                    return ResponseEntity.ok(savedPayment);
                } else {
                    logger.error("Failed to update payment with ID: {}", id);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }

            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

        } else {
            logger.warn("Payment with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {

        logger.info("Deleting payment with ID: {}", id);

        Payment payment = paymentService.getPaymentById(id);

        if (payment != null) {

            Long adminId = payment.getUser().getId();

            logger.info("Admin ID in session: {}", adminId);

            if (adminId == null) {
                // Handle the case where the admin ID is not found in the session
                logger.error("Admin ID not found in session");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Users admin = userService.getUserById(adminId);

            if (admin != null && admin.getRole() == UserRole.ADMIN) {

                paymentService.deletePayment(id);
                logger.info("Payment deleted successfully with ID: {}", id);

                // Log user action using UserListener
                userListener.logUserAction(admin, "Deleted payment with ID: " + id);

                return ResponseEntity.noContent().build();

            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            logger.warn("Payment with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }
}
