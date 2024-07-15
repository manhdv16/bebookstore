package dvm.springbootweb.service;

import dvm.springbootweb.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService {
    List<OrderDetail> findAllOrderDetail(int orderId);
}
