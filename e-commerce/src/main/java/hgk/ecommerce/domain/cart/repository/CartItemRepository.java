package hgk.ecommerce.domain.cart.repository;

import hgk.ecommerce.domain.cart.Cart;
import hgk.ecommerce.domain.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("select ci from CartItem ci join fetch ci.item where ci.cart.id = :cartId")
    List<CartItem> findCartItemsFetchItemsByCart(@Param("cartId") Long cartId);

    void deleteCartItemsByCart(Cart cart);
}
