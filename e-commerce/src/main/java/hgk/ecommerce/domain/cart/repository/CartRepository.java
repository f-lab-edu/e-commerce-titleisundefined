package hgk.ecommerce.domain.cart.repository;

import hgk.ecommerce.domain.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findCartByUserId(Long userId);
}
