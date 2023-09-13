package hgk.ecommerce.domain.order.controller;

import hgk.ecommerce.domain.common.annotation.AuthCheck;
import hgk.ecommerce.domain.order.dto.response.OrderInfo;
import hgk.ecommerce.domain.order.service.OrderService;
import hgk.ecommerce.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public List<OrderInfo> getOrders(@AuthCheck User user,
                                     @RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "5") Integer count) {
        return orderService.getOrderInfo(user, page, count);
    }

    @GetMapping("/{orderId}")
    public OrderInfo getOrderDetails(@AuthCheck User user, @PathVariable Long orderId) {
        return orderService.getOrderDetail(user, orderId);
    }

    @PostMapping
    public void proceedOrder(@AuthCheck User user) {
        orderService.order(user);
    }

    @DeleteMapping
    public void cancelOrder(@AuthCheck User user, Long orderId) {
        orderService.cancelOrder(user, orderId);
    }
}
