package hgk.ecommerce.domain.order.repository;

import hgk.ecommerce.domain.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("select oi from OrderItem oi join fetch oi.item where oi.order.id = :orderId order by oi.modifyDate")
    List<OrderItem> findOrderItemsFetchItemsByOrderId(@Param("orderId") Long orderId);
}
