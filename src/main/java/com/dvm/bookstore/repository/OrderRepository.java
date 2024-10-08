package com.dvm.bookstore.repository;

import com.dvm.bookstore.entity.Order;
import com.dvm.bookstore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> getAllByUser(User user);
    @Query("select od from Order od order by od.orderDate desc")
    Page<Order> findOrders(Pageable page);
}
