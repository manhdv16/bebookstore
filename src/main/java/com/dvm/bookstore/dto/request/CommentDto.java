package com.dvm.bookstore.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Comment
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentDto {
    private int rating;
    private String content;
    private int orderId;
}
