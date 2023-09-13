package hgk.ecommerce.domain.order.service;

import hgk.ecommerce.domain.cart.CartItem;
import hgk.ecommerce.domain.cart.service.CartService;
import hgk.ecommerce.domain.common.exceptions.AuthenticationException;
import hgk.ecommerce.domain.common.exceptions.InvalidRequest;
import hgk.ecommerce.domain.common.exceptions.NoResourceException;
import hgk.ecommerce.domain.item.Item;
import hgk.ecommerce.domain.item.service.ItemService;
import hgk.ecommerce.domain.order.Order;
import hgk.ecommerce.domain.order.OrderItem;
import hgk.ecommerce.domain.order.dto.enums.OrderStatus;
import hgk.ecommerce.domain.order.dto.response.OrderDetail;
import hgk.ecommerce.domain.order.dto.response.OrderInfo;
import hgk.ecommerce.domain.order.repository.OrderItemRepository;
import hgk.ecommerce.domain.order.repository.OrderRepository;
import hgk.ecommerce.domain.payment.service.PaymentService;
import hgk.ecommerce.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final PaymentService paymentService;
    private final ItemService itemService;

    @Transactional(readOnly = true)
    public List<OrderInfo> getOrderInfo(User user, Integer page, Integer count) {
        PageRequest pageRequest = PageRequest.of(page - 1, count, Sort.Direction.DESC, "createDate");
        Page<Order> orders = orderRepository.findAll(pageRequest);

        return orders.stream().map(OrderInfo::new).toList();
    }

    @Transactional(readOnly = true)
    public OrderInfo getOrderDetail(User user, Long orderId) {
        Order order = getOrderById(orderId);

        checkOrderAuth(user, order);
        List<OrderItem> orderItems = orderItemRepository.findOrderItemsFetchItemsByOrderId(orderId);

        return new OrderDetail(order, orderItems);
    }

    @Transactional
    public void order(User user) {
        List<CartItem> cartItems = cartService.getCartItemsEntityFetchItemByCart(user);

        if(cartItems.isEmpty()) {
            throw new InvalidRequest("카트가 비어있습니다.", HttpStatus.BAD_REQUEST);
        }

        int totalPrice = cartItemsTotalPrice(cartItems);

        paymentService.decreasePoint(user, totalPrice);
        proceedOrderFromCartItems(user, cartItems);

        cartService.clearCart(user);
    }

    @Transactional
    public void cancelOrder(User user, Long orderId) {
        Order order = getOrderById(orderId);

        checkOrderAuth(user, order);

        if(order.getOrderStatus().equals(OrderStatus.CANCEL)) {
            throw new InvalidRequest("이미 취소된 주문입니다.", HttpStatus.BAD_REQUEST);
        }

        List<OrderItem> orderItems = orderItemRepository.findOrderItemsFetchItemsByOrderId(order.getId());
        int totalPrice = orderItemsTotalPrice(orderItems);

        paymentService.increasePoint(user, totalPrice);
        cancelOrderFromOrderItems(orderItems);
    }

    //region PRIVATE METHOD

    private void checkOrderAuth(User user, Order order) {
        if(!order.getUser().getId().equals(user.getId())) {
            throw new AuthenticationException("접근 권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> {
            throw new NoResourceException("주문 내역을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
        });
    }

    private int cartItemsTotalPrice(List<CartItem> cartItems) {
        return cartItems.stream()
                .mapToInt(ci -> ci.getItem().getPrice() * ci.getQuantity())
                .sum();
    }

    private int orderItemsTotalPrice(List<OrderItem> orderItems) {
        return orderItems.stream().mapToInt(oi -> oi.getTotalPrice()).sum();
    }

    private void proceedOrderFromCartItems(User user, List<CartItem> cartItems) {
        Order order = Order.createOrder(user);
        orderRepository.save(order);
        orderCartItems(cartItems, order);
    }

    private void cancelOrderFromOrderItems(List<OrderItem> orderItems) {
        orderItems.stream().forEach(oi ->
                itemService.increaseStock(oi.getItem().getId(), oi.getQuantity())
        );
    }

    private void orderCartItems(List<CartItem> cartItems, Order order) {
        cartItems.stream().forEach(ci -> {
            OrderItem orderItem = OrderItem.createOrderItem(order, ci.getItem(), ci.getQuantity());
            itemService.decreaseStock(ci.getItem().getId(), ci.getQuantity());
            orderItemRepository.save(orderItem);
        });
    }

    //endregion
}
