package com.dvm.bookstore.controller;

import com.dvm.bookstore.dto.request.CommentDto;
import com.dvm.bookstore.dto.response.APIResponse;
import com.dvm.bookstore.entity.Comment;
import com.dvm.bookstore.entity.Order;
import com.dvm.bookstore.entity.OrderDetail;
import com.dvm.bookstore.entity.User;
import com.dvm.bookstore.jwt.JwtTokenProvider;
import com.dvm.bookstore.service.CommentService;
import com.dvm.bookstore.service.OrderDetailService;
import com.dvm.bookstore.service.OrderService;
import com.dvm.bookstore.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * CommentController
 */
@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("api/v1")
public class CommentController {
    CommentService commentService;
    JwtTokenProvider jwtTokenProvider;
    UserService userService;
    OrderDetailService detailService;
    OrderService orderService;
    static Logger LOGGER = LogManager.getLogger(CommentController.class);

    /**
     * Get all comments
     */
    @GetMapping("/comments")
    public APIResponse<?> findAll() {
        List<Comment> listComments = commentService.findAll();
        return APIResponse.builder()
                .code(200)
                .message("Get all comments successfully")
                .data(listComments).build();
    }
    @GetMapping("/searchCommentByUser")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public APIResponse<?> findAllByUserId(@RequestParam int userId) {
        Set<Comment> listComments = commentService.findAllCommentByUserId(userId);
        return APIResponse.builder()
                .code(200)
                .message("Get all comments by user successfully")
                .data(listComments).build();
    }
    @GetMapping("/searchCommentByBook/{id}")
    public APIResponse<?> findAllByBookId(@PathVariable int id) {
        Set<Comment> listComments = commentService.findAllCommentByBookId(id);
        return APIResponse.builder()
                .code(200)
                .message("Get all comments by book successfully")
                .data(listComments).build();
    }
    /**
     * Add comment for each book in order
     * @param token
     * @param dto
     * @return message
     */
    @PostMapping("/addComment")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public APIResponse<?> addComment(@RequestHeader("Authorization") String token, @RequestBody CommentDto dto) {
        // Get user from token
        String userName = jwtTokenProvider.getUserNameFromJwt(token.substring(7));
        User user = userService.findByUserName(userName);
        if (user == null) {
            LOGGER.error("User not found with username: "+userName);
            return APIResponse.builder()
                    .code(404)
                    .message("User not found")
                    .build();
        }
        Order order = orderService.findById(dto.getOrderId());
        if (order == null) {
            LOGGER.error("Order not found with id: "+dto.getOrderId());
            return APIResponse.builder()
                    .code(404)
                    .message("Order not found")
                    .build();
        }
        List<OrderDetail> list = detailService.findAllOrderDetail(dto.getOrderId());
        // add comment for each book in order
        Comment comment;
        for(OrderDetail d : list){
            comment = Comment.builder()
                    .rating(dto.getRating())
                    .content(dto.getContent())
                    .cmtDate(LocalDate.now())
                    .user(user)
                    .book(d.getBook())
                    .build();
            commentService.save(comment);
        }
        // update order status
        order.setReviewed(true);
        orderService.update(order);
        return APIResponse.builder()
                .code(200)
                .message("Comment added successfully")
                .build();
    }
    /**
     * Add comment for each book in order
     */
    @PutMapping("/updateComment/{id}")
    public APIResponse<?> update(@PathVariable int id, @RequestParam(required = false) String content) {
        Comment comment = commentService.findById(id);
        if (comment == null) {
            LOGGER.error("Comment not found with id: "+id);
            return APIResponse.builder()
                    .code(404)
                    .message("Comment not found")
                    .build();
        }
        if (content != null) comment.setContent(content);
        commentService.save(comment);
        return APIResponse.builder()
                .code(200)
                .message("Comment updated successfully")
                .build();
    }
    /**
     * Delete comment
     */
    @DeleteMapping("/deleteComment/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    public APIResponse<?> delete(@PathVariable int id) {
       commentService.delete(id);
        return APIResponse.builder()
                .code(200)
                .message("Comment deleted successfully")
                .build();
    }
}
