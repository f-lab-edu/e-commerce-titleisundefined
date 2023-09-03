package hgk.ecommerce.domain.order;

import hgk.ecommerce.domain.common.entity.EntityBase;
import hgk.ecommerce.domain.common.exception.InvalidRequest;
import hgk.ecommerce.domain.item.Item;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "order_item")
public class OrderItem extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public static OrderItem createOrderItem(Order order, Item item, int count) {
        if(count <= 0) {
            throw new InvalidRequest("1개 이상 주문해야 합니다.", HttpStatus.BAD_REQUEST);
        }
        OrderItem orderItem = new OrderItem();
        orderItem.quantity = count;
        orderItem.item = item;
        orderItem.order = order;
        orderItem.totalPrice = item.getPrice() * count;
        return orderItem;
    }
}
