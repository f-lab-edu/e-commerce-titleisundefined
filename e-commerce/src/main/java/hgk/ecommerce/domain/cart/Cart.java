package hgk.ecommerce.domain.cart;

import hgk.ecommerce.domain.common.entity.EntityBase;
import hgk.ecommerce.domain.user.User;
import jakarta.persistence.*;
import jdk.jshell.spi.ExecutionControl;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cart")
public class Cart extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private List<CartItem> cartItems = new ArrayList<>();

    public static Cart createCart(User user) {
        Cart cart = new Cart();
        cart.user = user;
        return cart;
    }

    @Transactional
    public void addCartItem(CartItem cartItem) {
        Optional<CartItem> optionalCartItem = findCartItem(cartItem);

        if(optionalCartItem.isPresent()) {
            CartItem currentCartItem = optionalCartItem.get();
            currentCartItem.increaseQuantity(cartItem.getQuantity());
        } else {
            cartItems.add(cartItem);
        }
    }

    // TODO : Decrease Method 구현 필요

    public void removeCartItem(Long cartItemId) {
        this.cartItems.removeIf(ci -> ci.getId().equals(cartItemId));
    }

    private Optional<CartItem> getCartItem(CartItem cartItem) {
        Optional<CartItem> optionalCartItem = cartItems.stream()
                .filter(ci -> ci.getId().equals(cartItem.getId()))
                .findAny();
        return optionalCartItem;
    }

    private Optional<CartItem> findCartItem(CartItem cartItem) {
        return this.cartItems
                .stream()
                .filter(ci -> ci.getItem().getId().equals(cartItem.getItem().getId()))
                .findAny();
    }
}
