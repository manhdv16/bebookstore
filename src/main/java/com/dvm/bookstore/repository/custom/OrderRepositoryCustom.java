package com.dvm.bookstore.repository.custom;

import com.dvm.bookstore.dto.request.OrderSearchRequest;
import com.dvm.bookstore.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {
    PageResponse<?> getListOrderBySearchPagingAndSorting(OrderSearchRequest request, int pageSize, int pageNo);
}
