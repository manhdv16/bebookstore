package com.dvm.bookstore.dto;

import com.dvm.bookstore.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Data Transfer Object for Category
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private String categoryName;
    private String description;
    private Set<Book> listBooks = new HashSet<>();
}