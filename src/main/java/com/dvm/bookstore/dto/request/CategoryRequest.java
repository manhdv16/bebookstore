package com.dvm.bookstore.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {
    private String categoryName;
    private String description;
}
