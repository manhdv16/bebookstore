package com.dvm.bookstore.dto.response;

import com.dvm.bookstore.entity.Category;
import com.dvm.bookstore.entity.Comment;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookDetailResponse implements Serializable {
    private int id;
    private String bookName;
    private String description;
    private String author;
    private String image;
    private double price;
    private int quantity;
    private int sold;
    private String categoryName;
}
