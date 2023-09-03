package hgk.ecommerce.domain.cart.repository;

import hgk.ecommerce.domain.cart.Cart;
import hgk.ecommerce.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("select c from Cart c join fetch c.cartItems where c.user.id = :userId")
    Optional<Cart> findCartFetchCartItems(@Param("userId") Long userId);

    Optional<Cart> findCartByUser(User user);
}
