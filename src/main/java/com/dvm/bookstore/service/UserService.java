package com.dvm.bookstore.service;

import com.dvm.bookstore.entity.User;
import com.dvm.bookstore.dto.request.SignupRequest;

public interface UserService {
    User findByUserName(String username);
    User saveOrUpdate(User user);
    Boolean existsByUserName(String username);
    Boolean existsByEmail(String email);
    User signUp(SignupRequest request);
}
