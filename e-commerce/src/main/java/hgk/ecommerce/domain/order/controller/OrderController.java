package hgk.ecommerce.domain.order.controller;

import hgk.ecommerce.domain.common.annotation.AuthCheck;
import hgk.ecommerce.domain.order.dto.OrderItemResponse;
import hgk.ecommerce.domain.order.dto.OrderResponse;
import hgk.ecommerce.domain.order.service.OrderService;
import hgk.ecommerce.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public List<OrderItemResponse> getOrderDetail(@PathVariable Long orderId,
                                                  @AuthCheck User user) {
        return orderService.getOrderDetail(user, orderId);
    }

    @GetMapping
    public List<OrderResponse> getOrders(@AuthCheck User user,
                                         @RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "5") Integer count) {
        return orderService.getOrders(user, 1, 5);
    }

    @PostMapping
    public void order(@AuthCheck User user) {
        orderService.createOrder(user);
    }

    @DeleteMapping("/{orderId}")
    public void cancel(@PathVariable Long orderId,
                       @AuthCheck User user) {

        orderService.cancelOrder(user, orderId);
    }
}
