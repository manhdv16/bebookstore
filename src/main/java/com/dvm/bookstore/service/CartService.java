package com.dvm.bookstore.service;

import com.dvm.bookstore.dto.CartDto;
import com.dvm.bookstore.entity.Cart;
import com.dvm.bookstore.entity.User;

import java.util.List;

public interface CartService {
    Cart AddToCart(CartDto cartDto, User user);
    List<Cart> getAllCartByUserName(String userName);
    List<Cart> getAllCart();
    void deleteCartByBookId(Integer bookId);
    void deleteAllCart(Integer userId);
}
