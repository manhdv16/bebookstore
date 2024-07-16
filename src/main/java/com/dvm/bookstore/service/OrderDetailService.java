package com.dvm.bookstore.service;

import com.dvm.bookstore.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService {
    List<OrderDetail> findAllOrderDetail(int orderId);
}
