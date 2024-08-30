package com.dvm.bookstore.service.Impl;

import com.dvm.bookstore.dto.request.CartDto;
import com.dvm.bookstore.repository.BookRepository;
import com.dvm.bookstore.repository.CartRepository;
import com.dvm.bookstore.repository.UserRepository;
import com.dvm.bookstore.service.BookService;
import com.dvm.bookstore.service.CartService;
import com.dvm.bookstore.entity.Cart;
import com.dvm.bookstore.entity.User;
import com.dvm.bookstore.service.UserService;
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
    private final BookService bookService;
    private final UserService userService;
    /**
     * AddToCart method is used to add a book to cart
     * @param cartDto
     * @param user
     * @return Cart
     */
    @Override
    public Cart AddToCart(CartDto cartDto, String userName) {
        User user = userService.findByUserName(userName);
        if(cartRepository.exitsCartByUerId(user.getUserId())>0){
            Cart cart = cartRepository.findCartByBookId(cartDto.getBookId());
            if(cart != null){
                cart.setQuantity(cart.getQuantity()+cartDto.getQuantity());
                cartRepository.save(cart);
                return cart;
            }
        }
        Cart cart = Cart.builder()
                .book(bookService.findByBookId(cartDto.getBookId()))
                .user(user)
                .quantity(cartDto.getQuantity())
                .build();
        cartRepository.save(cart);
        return cart;
    }

    @Override
    public List<Cart> getAllCartByUserName(String userName) {
        return cartRepository.findAllByUserName(userName);
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
