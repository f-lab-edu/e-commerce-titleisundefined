package hgk.ecommerce.domain.cart.service;

import hgk.ecommerce.domain.cart.Cart;
import hgk.ecommerce.domain.cart.CartItem;
import hgk.ecommerce.domain.cart.dto.CartItemResponse;
import hgk.ecommerce.domain.cart.dto.CartItemSave;
import hgk.ecommerce.domain.cart.repository.CartItemRepository;
import hgk.ecommerce.domain.cart.repository.CartRepository;
import hgk.ecommerce.domain.common.exception.InvalidRequest;
import hgk.ecommerce.domain.item.Item;
import hgk.ecommerce.domain.item.dto.ItemStatus;
import hgk.ecommerce.domain.item.service.ItemService;
import hgk.ecommerce.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemService itemService;

    @Transactional(readOnly = true)
    public List<CartItemResponse> getCartItemResponses(User user) {
        Cart cart = getCart(user);
        List<CartItem> cartItems = getCartItemsFetchItemsByCart(cart);
        List<CartItemResponse> cartItemResponses = getCartResponseDto(cartItems);

        return cartItemResponses;
    }

    @Transactional
    public void addToCart(User user, CartItemSave cartItemSave) {
        Cart cart = getCart(user);
        List<CartItem> cartItems = getCartItemsFetchItemsByCart(cart);

        Optional<CartItem> optionalCartItem = cartItems.stream()
                .filter(eqItemPredicate(cartItemSave.getItemId()))
                .findAny();

        optionalCartItem.ifPresentOrElse(
                (ci) -> ci.increaseQuantity(cartItemSave.getQuantity()),    // 존재할 경우 수량 증가
                () -> createCartItem(cartItemSave, cart)                    // 존재하지 않을 경우 카트에 추가;
        );
    }

    @Transactional
    public void removeCart(User user, Long cartItemId) {
        Optional<Cart> optionalCart = cartRepository.findCartFetchCartItems(user.getId());

        optionalCart.ifPresent((cart) -> {
            Optional<CartItem> any = cart.getCartItems().stream().filter(a -> a.getId().equals(cartItemId))
                    .findAny();
            any.ifPresent(ci -> cartItemRepository.delete(ci));
        });

    }

    @Transactional
    public Cart getCart(User user) {
        Cart cart = cartRepository.findCartByUser(user).orElse(null);

        if (cart == null) {
            cart = Cart.createCart(user);
            return cartRepository.save(cart);
        }

        return cart;
    }

    @Transactional
    public List<CartItem> getCartItemsFetchItemsByCart(Cart cart) {
        return cartItemRepository.findCartItemsFetchItemsByCart(cart.getId());
    }


    //region PRIVATE METHOD

    private List<CartItemResponse> getCartResponseDto(List<CartItem> cartItems) {
        return cartItems.stream().map(CartItemResponse::new)
                .toList();
    }

    private Predicate<CartItem> eqItemPredicate(Long itemId) {
        return ci -> ci.getItem().getId().equals(itemId);
    }

    private void createCartItem(CartItemSave cartItemSave, Cart cart) {
        Item item = itemService.getItemEntity(cartItemSave.getItemId());

        if (!item.getStatus().equals(ItemStatus.ACTIVE)) {
            throw new InvalidRequest("판매가 중지된 상품입니다.", HttpStatus.BAD_REQUEST);
        }

        CartItem cartItem = CartItem.createCartItem(cart, item, cartItemSave.getQuantity());
        cartItemRepository.save(cartItem);
    }

    //endregion
}
