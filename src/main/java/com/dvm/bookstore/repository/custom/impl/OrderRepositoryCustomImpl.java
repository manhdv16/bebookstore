package com.dvm.bookstore.repository.custom.impl;

import com.dvm.bookstore.dto.request.OrderSearchRequest;
import com.dvm.bookstore.dto.response.OrderResponse;
import com.dvm.bookstore.dto.response.PageResponse;
import com.dvm.bookstore.repository.custom.OrderRepositoryCustom;
import com.dvm.bookstore.utils.DataUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public PageResponse<?> getListOrderBySearchPagingAndSorting(OrderSearchRequest request, int pageSize, int pageNo) {
        log.info("excute search multiple field order");

        Map<String, Object> params = new HashMap<>();

        StringBuilder sqlQuery = new StringBuilder(
                "select new com.dvm.bookstore.dto.response.OrderResponse(o.orderId,o.orderDate,o.totalBook,o.image,o.totalAmount,o.status,o.reviewed) from Order o where 1=1 ");

        if (DataUtils.isValidLocalDate(request.getOrderDate())) {
            sqlQuery.append(" and o.orderDate = :orderDate");
            params.put("orderDate", request.getOrderDate());
        }
        if (DataUtils.isValidString(request.getStatus())) {
            sqlQuery.append(" and o.status = :status");
            params.put("status", request.getStatus());
        }
        if (DataUtils.isValidBoolean(request.getReviewed())) {
            sqlQuery.append(" and o.reviewed = :reviewed");
            params.put("reviewed", request.getReviewed());
        }
        if(DataUtils.isValidDouble(request.getTotalAmount())) {
            sqlQuery.append(" and o.totalAmount = :totalAmount");
            params.put("totalAmount", request.getTotalAmount());
        }
        if(DataUtils.isValidInteger(request.getTotalBook())) {
            sqlQuery.append(" and o.totalBook = :totalBook");
            params.put("totalBook", request.getTotalBook());
        }

        Query selectQuery = entityManager.createQuery(sqlQuery.toString());

        selectQuery.setFirstResult(pageNo*pageSize);
        selectQuery.setMaxResults(pageSize);
        params.forEach(selectQuery::setParameter);

        List<OrderResponse> orderResponses = selectQuery.getResultList();

        return PageResponse.builder()
                .page(pageNo)
                .size(pageSize)
                .items(orderResponses)
                .build();
    }
}
