package com.dvm.bookstore.service;

import com.dvm.bookstore.dto.request.SignupRequest;
import com.dvm.bookstore.dto.request.UserRequest;
import com.dvm.bookstore.entity.User;

public interface UserService {
    User findByUserName(String username);
    User update(String username, UserRequest request);
    void saveOrUpdate(User user);
    Boolean existsByUserName(String username);
    Boolean existsByEmail(String email);
    User signUp(SignupRequest request);
    void updatePassword(String username, String oldPass, String newPass);
    void sendNewPasswordForUser(String email, User user);
}
