package com.dvm.bookstore.service.Impl;

import com.dvm.bookstore.dto.request.OrderDto;
import com.dvm.bookstore.entity.*;
import com.dvm.bookstore.repository.OrderDetailRepository;
import com.dvm.bookstore.repository.OrderRepository;
import com.dvm.bookstore.service.BookService;
import com.dvm.bookstore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository detailRepository;
    private final BookService bookService;
    @Override
    public List<Order> getAllOrderByUser(User user) {
        return orderRepository.getAllByUser(user);
    }

    /**
     * Save order
     * @param orderDto
     * @param user
     */
    @Override
    public void save(OrderDto orderDto, User user) {
        Order order = Order.builder()
                .orderDate(new Date())
                .user(user)
                .totalAmount(orderDto.getTotalCost())
                .totalBook(orderDto.getListCart().size())
                .image(orderDto.getListCart().get(0).getBook().getImage())
                .status(EStatus.PROCESSING.toString())
                .reviewed(false)
                .build();

        orderRepository.save(order);

        OrderDetail detail;
        Book book;
        for (Cart c : orderDto.getListCart()) {
            book = c.getBook();
            book.setSold(book.getSold() + c.getQuantity());
            detail = OrderDetail.builder()
                    .book(book)
                    .quantity(c.getQuantity())
                    .orderId(order.getOrderId())
                    .build();
            bookService.save(book);
            detailRepository.save(detail);
        }
    }
    @Override
    public void update(Order order) {
        orderRepository.save(order);
    }

    @Override
    public Order findById(int id) {
        return orderRepository.findById(id).get();
    }

    @Override
    public void deleteOrder(int id) {
        orderRepository.deleteById(id);
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Page<Order> getPagging(Pageable pageable) {
        return orderRepository.findOrders(pageable);
    }
}
