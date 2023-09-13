package hgk.ecommerce.domain.order.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import hgk.ecommerce.domain.order.Order;
import hgk.ecommerce.domain.order.dto.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderInfo {

    private Long orderId;
    private OrderStatus orderStatus;
    private LocalDateTime modifyDate;

    public OrderInfo(Order order) {
        orderId = order.getId();
        orderStatus = order.getOrderStatus();
        modifyDate = order.getModifyDate();
    }
}
