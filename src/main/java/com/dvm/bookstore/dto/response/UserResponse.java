package com.dvm.bookstore.dto.response;

import com.dvm.bookstore.entity.Comment;
import com.dvm.bookstore.entity.Order;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String username;
    String email;
    List<Order> orders;
    List<Comment> comments;
}
