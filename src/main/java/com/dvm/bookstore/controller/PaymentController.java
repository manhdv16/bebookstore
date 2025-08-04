package com.dvm.bookstore.controller;

import com.dvm.bookstore.dto.response.APIResponse;
import com.dvm.bookstore.entity.Payment;
import com.dvm.bookstore.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class PaymentController {

    private final PaymentRepository paymentRepository;

    @PostMapping("/create-payment")
    public APIResponse createPayment() {
        // Logic to create a payment
        Payment payment = Payment.builder()
                .id(UUID.randomUUID())
                .orderId("12345")
                .paymentMethod("Credit Card")
                .paymentStatus("Completed")
                .paymentDate("2023-10-01")
                .paymentAmount("100.00")
                .build();
        paymentRepository.save(payment);
        return APIResponse.builder()
                .code(200)
                .message("Payment created successfully")
                .data(payment)
                .build();
    }
    @PutMapping("/update-payment")
    public APIResponse updatePayment(@PathVariable UUID id, @RequestBody Payment paymentDetails) {
        Payment payment = paymentRepository.findById(id.toString()).orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setOrderId(paymentDetails.getOrderId());
        payment.setPaymentMethod(paymentDetails.getPaymentMethod());
        payment.setPaymentStatus(paymentDetails.getPaymentStatus());
        payment.setPaymentDate(paymentDetails.getPaymentDate());
        payment.setPaymentAmount(paymentDetails.getPaymentAmount());
        paymentRepository.save(payment);
        return APIResponse.builder()
                .code(200)
                .message("Payment updated successfully")
                .data(payment)
                .build();

    }
}
