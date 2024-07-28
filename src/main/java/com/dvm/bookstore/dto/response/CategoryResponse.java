package com.dvm.bookstore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CategoryResponse {
    private int id;
    private String name;
    private String description;

}
