package hgk.ecommerce.domain.cart.controller;

import hgk.ecommerce.domain.cart.dto.request.CartItemSaveDto;
import hgk.ecommerce.domain.cart.dto.response.CartItemInfo;
import hgk.ecommerce.domain.cart.service.CartService;
import hgk.ecommerce.domain.common.annotation.AuthCheck;
import hgk.ecommerce.domain.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/cart")
@RestController
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public List<CartItemInfo> getCartItems(@AuthCheck User user) {
        return cartService.getCartItemsFetchItemByCart(user);
    }

    @PostMapping
    public void addCartItem(@AuthCheck User user, @Valid @RequestBody CartItemSaveDto cartItemSaveDto) {
        cartService.addCartItem(user, cartItemSaveDto);
    }

    @DeleteMapping("/{cartItemId}")
    public void deleteCartItem(@AuthCheck User user, @PathVariable Long cartItemId) {
        cartService.removeCartItem(user, cartItemId);
    }

    @DeleteMapping
    public void clearCart(@AuthCheck User user) {
        cartService.clearCart(user);
    }
}
