package com.dvm.bookstore.controller;

import com.dvm.bookstore.dto.request.CartDto;
import com.dvm.bookstore.dto.response.APIResponse;
import com.dvm.bookstore.entity.Cart;
import com.dvm.bookstore.jwt.JwtTokenProvider;
import com.dvm.bookstore.service.CartService;
import com.dvm.bookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Cart Controller
 */
@CrossOrigin("*")
@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class CartController {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final CartService cartService;
    private static final Logger LOGGER = LogManager.getLogger(CartController.class);
    /**
     * Add to cart
     * @param cartDto
     * @param token
     * @return cart
     */
    @PostMapping("/addToCart")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public APIResponse<?> addToCart(@RequestBody CartDto cartDto,
                                    @RequestHeader("Authorization") String token) {
        String userName = jwtTokenProvider.getUserNameFromJwt(token.substring(7));
        Cart cart = cartService.AddToCart(cartDto, userName);
        return APIResponse.builder()
                .code(200)
                .message("Add to cart successfully")
                .data(cart).build();
    }
    /**
     * view cart
     * @param token
     * @return list cart
     */
    @GetMapping("/viewCart")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public APIResponse<?> viewCart(@RequestHeader("Authorization") String token) {
        String userName = jwtTokenProvider.getUserNameFromJwt(token.substring(7));
        List<Cart> listCart = cartService.getAllCartByUserName(userName);
        if (ObjectUtils.isEmpty(listCart)) {
            return APIResponse.builder()
                    .code(404)
                    .message("Cart is empty")
                    .build();
        }
        return APIResponse.builder()
                .code(200)
                .message("Get all cart successfully")
                .data(listCart).build();
    }
    /**
     * delete cart
     * @param bookId
     * @return message
     */
    @DeleteMapping("/deleteCart/{bookId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public APIResponse<?> deleteCart(@PathVariable Integer bookId) {
        cartService.deleteCartByBookId(bookId);
        return APIResponse.builder()
                .code(200)
                .message("Delete cart successfully")
                .build();
    }
}
