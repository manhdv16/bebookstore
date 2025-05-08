package com.dvm.bookstore.repository.custom;

import com.dvm.bookstore.dto.request.BookSearchRequest;
import com.dvm.bookstore.dto.response.BookDetailResponse;
import com.dvm.bookstore.dto.response.PageResponse;

import java.util.List;

public interface BookRepositoryCustom {
    PageResponse<List<BookDetailResponse>> searchBook(int pageNo, int pageSize, String search, String sort);

    PageResponse<BookDetailResponse> searchBookWithQueryDSL(BookSearchRequest request);

}
