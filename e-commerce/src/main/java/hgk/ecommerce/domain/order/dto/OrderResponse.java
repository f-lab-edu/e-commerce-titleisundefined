package hgk.ecommerce.domain.order.dto;

import hgk.ecommerce.domain.order.Order;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderResponse {
    private Long orderId;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    public OrderResponse(Order order) {
        this.orderId = order.getId();
        this.createDate = order.getCreateDate();
        this.modifyDate = order.getModifyDate();
    }
}
