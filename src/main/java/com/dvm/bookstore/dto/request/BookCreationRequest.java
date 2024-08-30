package com.dvm.bookstore.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCreationRequest {
    private String bookName;
    private String description;
    private String author;
    private MultipartFile image;
    private double price;
    private int quantity;
    private int categoryId;
}
