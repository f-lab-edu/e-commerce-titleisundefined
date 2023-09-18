package hgk.ecommerce.domain.order.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import hgk.ecommerce.domain.order.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderItemResponse {
    private Long orderItemId;
    private Long itemId;
    private String itemName;
    private Integer quantity;
    private Integer totalPrice;

    public OrderItemResponse(OrderItem orderItem) {
        orderItemId = orderItem.getId();
        itemId = orderItem.getItem().getId();
        quantity = orderItem.getQuantity();
        totalPrice = orderItem.getTotalPrice();
        itemName = orderItem.getItem().getName();
    }

}
