package hgk.ecommerce.domain.cart.repository;

import hgk.ecommerce.domain.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("select c from Cart c join fetch c.cartItems ci join fetch ci.item where c.user.id = :userId")
    Optional<Cart> findCartFetchCartItemAndItem(@Param("userId") Long userId);
}
