package com.dvm.bookstore.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Data Transfer Object for Book
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookSearchRequest {
     String bookName;
     String author;
     Double minPrice;
     Double maxPrice;
     String categoryName;
     Integer sold;
     Integer pageNum;
     Integer pageSize;
     String direction;
     String sortField;
}
