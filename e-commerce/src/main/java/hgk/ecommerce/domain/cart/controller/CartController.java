package hgk.ecommerce.domain.cart.controller;

import hgk.ecommerce.domain.cart.dto.CartItemResponse;
import hgk.ecommerce.domain.cart.dto.CartItemSave;
import hgk.ecommerce.domain.cart.service.CartService;
import hgk.ecommerce.domain.common.annotation.AuthCheck;
import hgk.ecommerce.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public List<CartItemResponse> getCartItems(@AuthCheck User user) {
        return cartService.getCartItemResponses(user);
    }

    @PostMapping
    public void addCartItem(@AuthCheck User user, CartItemSave cartItemSave) {
        cartService.addToCart(user, cartItemSave);
    }

    @DeleteMapping
    public void removeCartItem(@AuthCheck User user, Long cartItemId) {
        cartService.removeCart(user, cartItemId);
    }
}
