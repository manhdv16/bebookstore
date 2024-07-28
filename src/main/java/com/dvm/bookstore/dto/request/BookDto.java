package com.dvm.bookstore.dto.request;

import com.dvm.bookstore.entity.Category;
import com.dvm.bookstore.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Data Transfer Object for Book
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {
    private int bookId;
    private String bookName;
    private String description;
    private String author;
    private String image;
    private double price;
    private int quantity;
    private int sold;
    private Category category;
    private Set<Comment> listComments;
}
