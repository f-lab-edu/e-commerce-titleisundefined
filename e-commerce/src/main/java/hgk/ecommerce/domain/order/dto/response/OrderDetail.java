package hgk.ecommerce.domain.order.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import hgk.ecommerce.domain.order.Order;
import hgk.ecommerce.domain.order.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail extends OrderInfo{
    private List<OrderItemResponse> orderItems;

    public OrderDetail(Order order, List<OrderItem> orderItems) {
        super(order);
        this.orderItems = parseOrderToItems(orderItems);
    }

    private List<OrderItemResponse> parseOrderToItems(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItemResponse::new)
                .toList();
//        return orderItems.stream()
//                .map(oi -> oi.getItem())
//                .map(ItemInfo::new)
//                .toList();
    }
}
