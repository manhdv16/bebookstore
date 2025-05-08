package com.dvm.bookstore.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderSearchRequest {
    LocalDate orderDate;
    Integer totalBook;
    Double totalAmount;
    String status;
    Boolean reviewed;
}
