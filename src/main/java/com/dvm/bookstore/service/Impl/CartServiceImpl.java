package com.dvm.bookstore.service.Impl;

import com.dvm.bookstore.dto.CartDto;
import com.dvm.bookstore.repository.BookRepository;
import com.dvm.bookstore.repository.CartRepository;
import com.dvm.bookstore.service.CartService;
import com.dvm.bookstore.entity.Cart;
import com.dvm.bookstore.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CartServiceImpl class implements CartService interface
 * @see CartService
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    /**
     * AddToCart method is used to add a book to cart
     * @param cartDto
     * @param user
     * @return Cart
     */
    @Override
    public Cart AddToCart(CartDto cartDto, User user) {
        Cart cart =null;
        if(cartRepository.exitsCartByUerId(user.getUserId())>0){
            cart = cartRepository.findCartByBookId(cartDto.getBookId());
        }
        if(cart != null){
            cart.setQuantity(cart.getQuantity()+cartDto.getQuantity());
            cartRepository.save(cart);
            return cart;
        }
        cart = new Cart();
        cart.setBook(bookRepository.findById(cartDto.getBookId()).get());
        cart.setUser(user);
        cart.setQuantity(cartDto.getQuantity());
        cartRepository.save(cart);
        return cart;
    }

    @Override
    public List<Cart> getAllCartByUserName(String userName) {
        List<Cart> listCart = cartRepository.findAllByUserName(userName);
        return listCart;
    }

    @Override
    public List<Cart> getAllCart() {
        return cartRepository.findAll();
    }

    @Override
    public void deleteCartByBookId(Integer bookId) {
        cartRepository.deleteCartByBookId(bookId);
    }

    @Override
    public void deleteAllCart(Integer userId) {
        cartRepository.deleteAllByUserUserId(userId);
    }
}