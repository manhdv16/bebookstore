package com.dvm.bookstore.dto.request;

import com.dvm.bookstore.entity.Cart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object for Order
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private List<Cart> listCart;
    private double totalCost;
}
