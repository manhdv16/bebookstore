package dvm.springbootweb.controller;

import dvm.springbootweb.dto.CartDto;
import dvm.springbootweb.entity.Cart;
import dvm.springbootweb.entity.User;
import dvm.springbootweb.jwt.JwtTokenProvider;
import dvm.springbootweb.payload.response.MessageResponse;
import dvm.springbootweb.service.CartService;
import dvm.springbootweb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * @return
     */
    @PostMapping("/addToCart")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<?> addToCart(@RequestBody CartDto cartDto,
                                       @RequestHeader("Authorization") String token) {
        String userName = jwtTokenProvider.getUserNameFromJwt(token.substring(7));
        if(userName== null){
            LOGGER.error("Token error with username: null");
            return ResponseEntity.status(400).body(new MessageResponse("Token error"));
        }
        User user = userService.findByUserName(userName);
        Cart cart = cartService.AddToCart(cartDto, user);
        return ResponseEntity.ok(cart);
    }
    /**
     * view cart
     * @param token
     * @return
     */
    @GetMapping("/viewCart")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<?> viewCart(@RequestHeader("Authorization") String token) {
        String token1 = token.substring(7);
        String userName = jwtTokenProvider.getUserNameFromJwt(token1);
        List<Cart> listCart = cartService.getAllCartByUserName(userName);
        if (ObjectUtils.isEmpty(listCart)) {
            return ResponseEntity.ok(new MessageResponse("Cart Null"));
        }
        return ResponseEntity.ok(listCart);
    }
    /**
     * delete cart
     * @param bookId
     * @param token
     * @return
     */
    @DeleteMapping("/deleteCart/{bookId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<?> deleteCart(@PathVariable Integer bookId, @RequestHeader("Authorization") String token) {
        String userName = jwtTokenProvider.getUserNameFromJwt(token.substring(7));
        if (userName == null) {
            LOGGER.error("Token error with username: null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Token error"));
        }
        cartService.deleteCartByBookId(bookId);
        return ResponseEntity.ok(new MessageResponse("Delete Successfully"));
    }
}
