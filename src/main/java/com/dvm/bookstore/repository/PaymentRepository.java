package com.dvm.bookstore.repository;

import com.dvm.bookstore.entity.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PaymentRepository extends MongoRepository<Payment, String> {
     List<Payment> findByOrderId(String orderId);

}
