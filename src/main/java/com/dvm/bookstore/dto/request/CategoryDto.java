package com.dvm.bookstore.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


/**
 * Data Transfer Object for Category
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    @NotBlank(message = "CATEGORY_NAME_INVALID")
    private String categoryName;
    @NotBlank(message = "DESCRIPTION_INVALID")
    private String description;
}
