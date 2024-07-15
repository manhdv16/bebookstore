package dvm.springbootweb.service.Impl;

import dvm.springbootweb.dto.OrderDto;
import dvm.springbootweb.entity.*;
import dvm.springbootweb.repository.OrderDetailRepository;
import dvm.springbootweb.repository.OrderRepository;
import dvm.springbootweb.service.BookService;
import dvm.springbootweb.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
        Order order = new Order();
        order.setOrderDate(new Date());
        order.setUser(user);
        order.setTotalAmount(orderDto.getTotalCost());
        order.setTotalBook(orderDto.getListCart().size());
        order.setImage(orderDto.getListCart().get(0).getBook().getImage());
        order.setStatus(EStatus.PROCESSING.toString());
        order.setReviewed(false);
        orderRepository.save(order);
        OrderDetail detail;
        Book book;
        for (Cart c : orderDto.getListCart()) {
            detail = new OrderDetail();
            book = c.getBook();
            book.setSold(book.getSold() + c.getQuantity());
            detail.setBook(book);
            detail.setQuantity(c.getQuantity());
            detail.setOrderId(order.getOrderId());
            bookService.saveOrUpdate(book);
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
