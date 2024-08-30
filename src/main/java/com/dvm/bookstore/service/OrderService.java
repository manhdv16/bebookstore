package com.dvm.bookstore.service;

import com.dvm.bookstore.dto.request.OrderDto;
import com.dvm.bookstore.entity.Order;
import com.dvm.bookstore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    List<Order> getAllOrderByUser(User user);
    void save(OrderDto orderDto, User user);
    void update(Order order);
    Order findById(int id);
    void deleteOrder(int id);
    List<Order> findAll();
    Page<Order> getPagging(Pageable pageable);
}
