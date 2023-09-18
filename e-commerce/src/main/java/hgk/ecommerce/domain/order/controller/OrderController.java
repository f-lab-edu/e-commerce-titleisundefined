package hgk.ecommerce.domain.order.controller;

import hgk.ecommerce.domain.common.annotation.AuthCheck;
import hgk.ecommerce.domain.order.dto.response.OrderDetail;
import hgk.ecommerce.domain.order.dto.response.OrderInfo;
import hgk.ecommerce.domain.order.service.OrderService;
import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.global.swagger.SwaggerConst;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static hgk.ecommerce.global.swagger.SwaggerConst.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "주문내역 조회", tags = USER)
    public List<OrderInfo> getOrders(@AuthCheck User user,
                                     @RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "5") Integer count) {
        return orderService.getOrderInfo(user, page, count);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "주문정보 조회", tags = USER)
    public OrderDetail getOrderDetails(@AuthCheck User user, @PathVariable Long orderId) {
        return orderService.getOrderDetail(user, orderId);
    }

    @PostMapping
    @Operation(summary = "장바구니 주문", tags = USER)
    public void proceedOrder(@AuthCheck User user) {
        orderService.order(user);
    }

    @DeleteMapping
    @Operation(summary = "주문 취소", tags = USER)
    public void cancelOrder(@AuthCheck User user, Long orderId) {
        orderService.cancelOrder(user, orderId);
    }
}
