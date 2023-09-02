package hgk.ecommerce.domain.cart.repository;

import hgk.ecommerce.domain.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
