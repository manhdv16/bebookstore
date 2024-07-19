package com.dvm.bookstore.controller;

import com.dvm.bookstore.dto.CommentDto;
import com.dvm.bookstore.entity.Comment;
import com.dvm.bookstore.entity.Order;
import com.dvm.bookstore.entity.OrderDetail;
import com.dvm.bookstore.entity.User;
import com.dvm.bookstore.jwt.JwtTokenProvider;
import com.dvm.bookstore.payload.response.MessageResponse;
import com.dvm.bookstore.service.CommentService;
import com.dvm.bookstore.service.OrderDetailService;
import com.dvm.bookstore.service.OrderService;
import com.dvm.bookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * CommentController
 */
@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class CommentController {
    private final CommentService commentService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final OrderDetailService detailService;
    private final OrderService orderService;
    private static final Logger LOGGER = LogManager.getLogger(CommentController.class);
    @GetMapping("/comments")
    public ResponseEntity<?> findAll() {
        List<Comment> listComments = commentService.findAll();
        return ResponseEntity.ok(listComments);
    }
    @GetMapping("/searchCommentByUser")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> findAllByUserId(@RequestParam int userId) {
        Set<Comment> listComments = commentService.findAllCommentByUserId(userId);
        return new ResponseEntity<>(listComments, HttpStatus.OK);
    }
    @GetMapping("/searchCommentByBook/{id}")
    public ResponseEntity<?> findAllByBookId(@PathVariable int id) {
        Set<Comment> listComments = commentService.findAllCommentByBookId(id);
        return new ResponseEntity<>(listComments, HttpStatus.OK);
    }
    /**
     * Add comment for each book in order
     * @param token
     * @param dto
     * @return message
     */
    @PostMapping("/addComment")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<?> addComment(@RequestHeader("Authorization") String token, @RequestBody CommentDto dto) {
        // Get user from token
        String userName = jwtTokenProvider.getUserNameFromJwt(token.substring(7));
        User user = userService.findByUserName(userName);
        if (user == null) {
            LOGGER.error("User not found with username: "+userName);
            return ResponseEntity.ok(new MessageResponse("User not found"));
        }
        Order order = orderService.findById(dto.getOrderId());
        if (order == null) {
            LOGGER.error("Order not found with id: "+dto.getOrderId());
            return ResponseEntity.ok(new MessageResponse("Order not found"));
        }
        List<OrderDetail> list = detailService.findAllOrderDetail(dto.getOrderId());
        // add comment for each book in order
        Comment comment;
        for(OrderDetail d : list){
            comment = new Comment();
            comment.setRating(dto.getRating());
            comment.setContent(dto.getContent());
            comment.setCmtDate(LocalDate.now());
            comment.setUser(user);
            comment.setBook(d.getBook());
            commentService.save(comment);
            System.out.println(d.getBook().getBookId());
        }
        // update order status
        order.setReviewed(true);
        orderService.update(order);
        return ResponseEntity.ok(new MessageResponse("Comment added successfully"));
    }
    /**
     * Add comment for each book in order
     */
    @PutMapping("/updateComment/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestParam(required = false) String content) {
        Comment comment = commentService.findById(id);
        if (comment == null) {
            LOGGER.error("Comment not found with id: "+id);
            return ResponseEntity.ok(new MessageResponse("Comment not found"));
        }
        if (content != null) comment.setContent(content);
        commentService.save(comment);
        return ResponseEntity.ok(new MessageResponse("Content has been updated"));
    }
    /**
     * Delete comment
     */
    @DeleteMapping("/deleteComment/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable int id) {
        boolean isDeleteSuccess = commentService.delete(id);
        if (!isDeleteSuccess) {
            LOGGER.error("Comment not found with id: "+id);
            return ResponseEntity.ok(new MessageResponse("Comment not found"));
        }
        return ResponseEntity.ok(new MessageResponse("Comment deleted"));
    }
}
