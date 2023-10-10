package hgk.ecommerce.domain.order.repository;

import hgk.ecommerce.domain.order.Order;
import hgk.ecommerce.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findOrdersByUser(User user, Pageable pageable);
}
