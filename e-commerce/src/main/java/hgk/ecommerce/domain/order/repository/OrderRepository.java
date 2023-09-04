package hgk.ecommerce.domain.order.repository;

import hgk.ecommerce.domain.order.Order;
import hgk.ecommerce.domain.order.OrderItem;
import hgk.ecommerce.domain.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("select o from Order o join fetch o.orderItems where o.id = :orderId")
    Optional<Order> findOrderFetchItems(@Param("orderId") Long orderId);

    List<Order> findOrdersByUser(User user, Pageable pageable);

    Optional<Order> findOrderByOrderItems(OrderItem orderItem);
}
