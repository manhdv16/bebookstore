package com.dvm.bookstore.service.Impl;

import com.dvm.bookstore.repository.OrderDetailRepository;
import com.dvm.bookstore.service.OrderDetailService;
import com.dvm.bookstore.entity.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * OrderDetailServiceImpl class implements OrderDetailService interface
 * @see OrderDetailService
 */
@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    @Autowired
    private OrderDetailRepository detailRepository;
    @Override
    public List<OrderDetail> findAllOrderDetail(int orderId) {
        return detailRepository.findAllByOrderId(orderId);
    }
}
