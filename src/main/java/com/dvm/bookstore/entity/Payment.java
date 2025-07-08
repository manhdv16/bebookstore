package com.dvm.bookstore.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Getter
@Setter
@Builder
@Document(collection = "payment")
public class Payment {
    @Id
    private UUID id;

    private String orderId;
    private String paymentMethod;
    private String paymentStatus;
    private String paymentDate;
    private String paymentAmount;
}
