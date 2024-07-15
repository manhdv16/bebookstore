package dvm.springbootweb.controller;

import dvm.springbootweb.dto.CommentDto;
import dvm.springbootweb.entity.Comment;
import dvm.springbootweb.entity.Order;
import dvm.springbootweb.entity.OrderDetail;
import dvm.springbootweb.entity.User;
import dvm.springbootweb.jwt.JwtTokenProvider;
import dvm.springbootweb.payload.response.MessageResponse;
import dvm.springbootweb.service.CommentService;
import dvm.springbootweb.service.OrderDetailService;
import dvm.springbootweb.service.OrderService;
import dvm.springbootweb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

        Order order = orderService.findById(dto.getOrderId());
        List<OrderDetail> list = detailService.findAllOrderDetail(dto.getOrderId());
        // add comment for each book in order
        Comment comment;
        for(OrderDetail d : list){
            comment = new Comment();
            comment.setRating(dto.getRating());
            comment.setContent(dto.getContent());
            comment.setCmtDate(new java.sql.Date(new Date().getTime()));
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
        if (content != null) comment.setContent(content);
        commentService.save(comment);
        return ResponseEntity.ok(new MessageResponse("Content has been updated"));
    }
    /**
     * Delete comment
     */
    @DeleteMapping("/deleteComment/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable int id) {
        commentService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Comment deleted"));
    }
}
