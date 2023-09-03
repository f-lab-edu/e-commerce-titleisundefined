package hgk.ecommerce.domain.order.dto;

import hgk.ecommerce.domain.order.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderItemResponse {
    private String name;
    private int price;
    private int quantity;

    public OrderItemResponse(OrderItem orderItem) {
        this.name = orderItem.getItem().getName();
        this.price = orderItem.getTotalPrice();
        this.quantity = orderItem.getQuantity();
    }
}
