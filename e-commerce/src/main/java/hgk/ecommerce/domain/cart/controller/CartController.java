package hgk.ecommerce.domain.cart.controller;

import hgk.ecommerce.domain.cart.dto.request.CartItemSaveDto;
import hgk.ecommerce.domain.cart.dto.response.CartItemInfo;
import hgk.ecommerce.domain.cart.service.CartService;
import hgk.ecommerce.domain.common.annotation.AuthCheck;
import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.global.swagger.SwaggerConst;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static hgk.ecommerce.global.swagger.SwaggerConst.*;

@RequestMapping("/cart")
@RestController
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    @Operation(summary = "장바구니 조회", tags = USER)
    public List<CartItemInfo> getCartItems(@AuthCheck(role = AuthCheck.Role.USER) Long userId) {
        return cartService.getCartItemsFetchItemByCart(userId);
    }

    @PostMapping
    @Operation(summary = "장바구니 추가", tags = USER)
    public void addCartItem(@AuthCheck(role = AuthCheck.Role.USER) Long userId, @Valid @RequestBody CartItemSaveDto cartItemSaveDto) {
        cartService.addCartItem(userId, cartItemSaveDto);
    }

    @DeleteMapping("/{cartItemId}")
    @Operation(summary = "장바구니 수량 감소", tags = USER)
    public void deleteCartItem(@AuthCheck(role = AuthCheck.Role.USER) Long userId, @PathVariable Long cartItemId) {
        cartService.removeCartItem(userId, cartItemId);
    }

    @DeleteMapping
    @Operation(summary = "장바구니 아이템 모두삭제", tags = USER)
    public void clearCart(@AuthCheck(role = AuthCheck.Role.USER) Long userId) {
        cartService.clearCart(userId);
    }
}
