package hgk.ecommerce.domain.cart.service;

import hgk.ecommerce.domain.cart.Cart;
import hgk.ecommerce.domain.cart.CartItem;
import hgk.ecommerce.domain.cart.dto.CartItemResponse;
import hgk.ecommerce.domain.cart.dto.CartItemSave;
import hgk.ecommerce.domain.cart.repository.CartRepository;
import hgk.ecommerce.domain.item.Item;
import hgk.ecommerce.domain.item.service.ItemService;
import hgk.ecommerce.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ItemService itemService;

    @Transactional(readOnly = true)
    public List<CartItemResponse> getCartItemResponses(User user) {
        Cart cart = getCartFetchItemsByUser(user);
        List<CartItemResponse> cartItems = getCartResponseDto(cart);

        return cartItems;
    }

    @Transactional
    public void addToCart(User user, CartItemSave cartItemSave) {
        Cart cart = getCartFetchItemsByUser(user);

        Item item = itemService.getItemEntity(cartItemSave.getItemId());
        CartItem cartItem = CartItem.createCartItem(cart, item, cartItemSave.getQuantity());

        cart.addCartItem(cartItem);
    }

    @Transactional
    public void removeCart(User user, Long cartItemId) {
        Cart cart = getCartFetchItemsByUser(user);
        cart.removeCartItem(cartItemId);
    }

    @Transactional
    public Supplier<? extends Cart> createCart(User user) {
        Cart cart = Cart.createCart(user);
        return () -> cartRepository.save(cart);
    }

    @Transactional(readOnly = true)
    public Cart getCartFetchItemsByUser(User user) {
        return cartRepository.findCartFetchCartItemAndItem(user.getId())
                .orElseGet(createCart(user));
    }

    //region PRIVATE METHOD

    private List<CartItemResponse> getCartResponseDto(Cart cart) {
        return cart.getCartItems()
                .stream()
                .map(CartItemResponse::new)
                .toList();
    }

    //endregion
}
