package com.dvm.bookstore.repository;

import com.dvm.bookstore.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail,Integer> {
    @Query("select o from OrderDetail o where o.orderId =?1")
    List<OrderDetail> findAllByOrderId(int orderId);
}
