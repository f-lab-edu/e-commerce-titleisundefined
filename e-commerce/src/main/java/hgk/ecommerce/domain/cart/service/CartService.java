package hgk.ecommerce.domain.cart.service;

import hgk.ecommerce.domain.cart.Cart;
import hgk.ecommerce.domain.cart.CartItem;
import hgk.ecommerce.domain.cart.dto.request.CartItemSaveDto;
import hgk.ecommerce.domain.cart.dto.response.CartItemInfo;
import hgk.ecommerce.domain.cart.repository.CartItemRepository;
import hgk.ecommerce.domain.cart.repository.CartRepository;
import hgk.ecommerce.domain.item.Item;
import hgk.ecommerce.domain.item.service.ItemService;
import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.domain.user.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemService itemService;
    private final UserServiceImpl userService;

    @Transactional
    public List<CartItemInfo> getCartItemsFetchItemByCart(Long userId) {
        User user = userService.getCurrentUserById(userId);
        Cart cart = getCartByUser(user);

        List<CartItem> cartItems = getCartItemsFetchItemByCart(cart);

        return cartItems.stream()
                .map(CartItemInfo::new)
                .toList();
    }

    @Transactional
    public List<CartItem> getCartItemsEntityFetchItemByCart(Long userId) {
        User user = userService.getCurrentUserById(userId);
        Cart cart = getCartByUser(user);
        return getCartItemsFetchItemByCart(cart);
    }

    @Transactional
    public Long addCartItem(Long userId, CartItemSaveDto cartItemSaveDto) {
        User user = userService.getCurrentUserById(userId);
        Cart cart = getCartByUser(user);

        Item item = itemService.getItemEntity(cartItemSaveDto.getItemId());

        List<CartItem> cartItems = getCartItemsFetchItemByCart(cart);
        Optional<CartItem> optionalCartItem = findCartItemByItem(cartItems, item);

        CartItem cartItem = null;

        if (optionalCartItem.isPresent()) {
            cartItem = optionalCartItem.get();
            cartItem.increaseQuantity(cartItemSaveDto.getCount());
        } else {
            cartItem = CartItem.createCartItem(cart, item, cartItemSaveDto.getCount());
            cartItemRepository.save(cartItem);
        }

        return cartItem.getId();
    }

    @Transactional
    public void removeCartItem(Long userId, Long cartItemId) {
        User user = userService.getCurrentUserById(userId);
        Cart cart = getCartByUser(user);
        List<CartItem> cartItems = getCartItemsFetchItemByCart(cart);

        Optional<CartItem> optionalCartItem = findCartItemById(cartItems, cartItemId);

        optionalCartItem.ifPresent(ci -> cartItemRepository.delete(ci));
    }

    @Transactional
    public void clearCart(Long userId) {
        User user = userService.getCurrentUserById(userId);
        Cart cart = getCartByUser(user);
        cartItemRepository.deleteCartItemsByCart(cart);
    }

    @Transactional
    protected Cart createCart(User user) {
        Cart cart = Cart.createCart(user);

        return cartRepository.save(cart);
    }


    //region PRIVATE METHOD

    private Optional<CartItem> findCartItemById(List<CartItem> cartItems, Long cartItemId) {
        return cartItems.stream()
                .filter(ci -> ci.getId().equals(cartItemId))
                .findAny();
    }

    private Optional<CartItem> findCartItemByItem(List<CartItem> cartItems, Item item) {
        return cartItems.stream()
                .filter(ci -> ci.getItem().getId().equals(item.getId()))
                .findAny();
    }

    @Transactional(readOnly = true)
    protected List<CartItem> getCartItemsFetchItemByCart(Cart cart) {
        return cartItemRepository.findCartItemsFetchItemsByCart(cart.getId());
    }

    @Transactional(readOnly = true)
    protected Cart getCartByUser(User user) {
        Optional<Cart> optionalCart = cartRepository.findCartByUserId(user.getId());
        if (optionalCart.isEmpty()) {
            return createCart(user);
        }
        return optionalCart.get();
    }

    //endregion
}
