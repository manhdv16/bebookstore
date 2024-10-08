package com.dvm.bookstore.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Cart
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartDto {
    private Integer bookId;
    @Min(0)
    private int quantity;
}
