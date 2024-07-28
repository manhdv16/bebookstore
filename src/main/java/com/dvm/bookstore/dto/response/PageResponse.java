package com.dvm.bookstore.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class PageResponse<T> implements Serializable {
    private int size;
    private int page;
    private int totalPages;
    private T items;
}
