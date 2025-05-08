package com.dvm.bookstore.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {
    private int orderId;
    private Date orderDate;
    private int totalBook;
    private String image;
    private double totalAmount;
    private String status;
    private boolean reviewed;
}
