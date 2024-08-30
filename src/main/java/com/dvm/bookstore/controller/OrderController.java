package com.dvm.bookstore.controller;

import com.dvm.bookstore.dto.request.OrderDto;
import com.dvm.bookstore.dto.response.APIResponse;
import com.dvm.bookstore.entity.Order;
import com.dvm.bookstore.entity.OrderDetail;
import com.dvm.bookstore.entity.User;
import com.dvm.bookstore.jwt.JwtTokenProvider;
import com.dvm.bookstore.dto.response.MessageResponse;
import com.dvm.bookstore.repository.OrderDetailRepository;
import com.dvm.bookstore.service.CartService;
import com.dvm.bookstore.service.OrderService;
import com.dvm.bookstore.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("api/v1")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class OrderController {
    OrderService orderService;
    JwtTokenProvider jwtTokenProvider;
    UserService userService;
    OrderDetailRepository detailRepository;
    CartService cartService;
    static Logger LOGGER = LogManager.getLogger(OrderController.class);

    /**
     * View order
     * @param token
     * @return
     */
    @GetMapping("/orders")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public APIResponse<?> viewOrder(@RequestHeader("Authorization") String token) {
        String userName = jwtTokenProvider.getUserNameFromJwt(token.substring(7));
        User user = userService.findByUserName(userName);
        List<Order> orderList = orderService.getAllOrderByUser(user);
        return APIResponse.builder()
                .code(200)
                .message("Get list order successfully")
                .data(orderList)
                .build();
    }
    /**
     * View pagging order
     * @param page
     * @return
     */
    @GetMapping("/page-order")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    public APIResponse<Map<String,Object>> viewPaggingOrder(@RequestParam int page){
        int size = 3;
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> pageCurrent = orderService.getPagging(pageable);
        Map<String, Object> data = new HashMap<>();
        data.put("orders", pageCurrent.getContent());
        data.put("totalPages", pageCurrent.getTotalPages());
        return APIResponse.<Map<String,Object>>builder()
                .code(200)
                .message("Get list order successfully")
                .data(data)
                .build();
    }
    /**
     * View order for admin
     * @param token
     * @return
     */
    @GetMapping("/adminOrder")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    public APIResponse<?> viewAdminOrder(@RequestHeader("Authorization") String token) {
        if(jwtTokenProvider.validateJwtToken(token.substring(7))) {
            List<Order> orderList = orderService.findAll();
            return APIResponse.builder()
                    .code(200)
                    .message("Get list order successfully")
                    .data(orderList)
                    .build();
        }
        LOGGER.error("token is not valid");
        return APIResponse.builder()
                .code(400)
                .message("token is not valid")
                .build();
    }
    /**
     * View order detail
     * @param orderId
     * @param token
     * @return
     */
    @GetMapping("order/{orderId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> viewOrderDetail(@PathVariable int orderId, @RequestHeader("Authorization")String token){
        List<OrderDetail> orDetailList = detailRepository.findAllByOrderId(orderId);
        return ResponseEntity.ok(orDetailList);
    }
    /**
     * Add to order
     * @param orderDto
     * @param token
     * @return
     */
    @PostMapping("/addToOrder")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<?> addToOrder(@RequestBody OrderDto orderDto, @RequestHeader("Authorization") String token) {
        String userName = jwtTokenProvider.getUserNameFromJwt(token.substring(7));
        if (userName == null) {
            LOGGER.error("token is not valid");
            return ResponseEntity.badRequest().body(new MessageResponse("token is not valid"));
        }
        User user = userService.findByUserName(userName);
//        save order and update sold
        orderService.save(orderDto, user);
//        delete cart
        cartService.deleteAllCart(user.getUserId());
        return ResponseEntity.ok(new MessageResponse("order added"));
    }
    /**
     * Update order
     * @param id
     * @param token
     * @param status
     * @return
     */
    @PutMapping("updateOrder/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<?> updateStatusOrder(@PathVariable int id, @RequestHeader("Authorization") String token, @RequestParam String status){
        boolean isValid = jwtTokenProvider.validateJwtToken(token.substring(7));
        if(isValid) {
            Order order = orderService.findById(id);
            order.setStatus(status);
            orderService.update(order);
            return ResponseEntity.ok(new MessageResponse("update order successfully"));
        }
        LOGGER.error("token is not valid");
        return ResponseEntity.badRequest().body(new MessageResponse("update not successfully"));
    }
    /**
     * Delete order
     * @param id
     * @return
     */
    @DeleteMapping("deleteOrder/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable int id){
        boolean isDeleteSuccess = orderService.deleteOrder(id);
        if (!isDeleteSuccess) {
            LOGGER.error("Order not found with id: "+id);
            return ResponseEntity.ok(new MessageResponse("Order not found"));
        }
        return ResponseEntity.ok(new MessageResponse("delete order successfully"));
    }
}
