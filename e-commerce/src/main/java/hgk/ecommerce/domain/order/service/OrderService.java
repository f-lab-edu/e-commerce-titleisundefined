package hgk.ecommerce.domain.order.service;

import hgk.ecommerce.domain.cart.Cart;
import hgk.ecommerce.domain.cart.CartItem;
import hgk.ecommerce.domain.cart.service.CartService;
import hgk.ecommerce.domain.common.exception.AuthenticationException;
import hgk.ecommerce.domain.common.exception.InvalidRequest;
import hgk.ecommerce.domain.common.exception.NoResourceException;
import hgk.ecommerce.domain.item.Item;
import hgk.ecommerce.domain.item.service.ItemService;
import hgk.ecommerce.domain.order.Order;
import hgk.ecommerce.domain.order.OrderItem;
import hgk.ecommerce.domain.order.dto.OrderItemResponse;
import hgk.ecommerce.domain.order.dto.OrderResponse;
import hgk.ecommerce.domain.order.dto.OrderStatus;
import hgk.ecommerce.domain.order.repository.OrderItemRepository;
import hgk.ecommerce.domain.order.repository.OrderRepository;
import hgk.ecommerce.domain.payment.service.PaymentService;
import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemService itemService;
    private final PaymentService paymentService;

    @Transactional(readOnly = true)
    public List<OrderItemResponse> getOrderDetail(User user, Long orderId) {
        Order order = getOrderDetail(orderId);
        checkAuthOrder(user, order);

        List<OrderItem> orderItems = orderItemRepository.findOrderItemsByOrderFetchItems(order.getId());

        return orderItemsToOrderItemResponse(orderItems);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrders(User user, Integer page, Integer count) {
        PageRequest paging = PageRequest.of(page - 1, count);
        List<Order> orders = orderRepository.findOrdersByUser(user, paging);

        return ordersToOrderResponses(orders);
    }

    @Transactional
    public void createOrder(User user) {
        Cart cart = cartService.getCart(user);
        List<CartItem> cartItems = cartService.getCartItemsFetchItemsByCart(cart);

        Order order = createAndSaveOrder(user);

        int totalPrice = calculateTotalPrice(cart);
        paymentService.decreasePoint(user, totalPrice);

        cartItems.forEach((ci) -> createAndSaveOrder(order, ci));
    }

    @Transactional
    public void cancelOrder(User user, Long orderId) {
        Order order = getOrderDetail(orderId);
        order.changeStatus(OrderStatus.CANCEL);

        int totalPrice = calculateTotalPrice(order);
        order.getOrderItems().forEach(oi -> decreaseItemStock(oi));

        paymentService.chargePoint(user, totalPrice);
    }

    @Transactional(readOnly = true)
    public OrderItem findOrderItemFetchItem(User user, Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findOrderItemFetchItemById(orderItemId).orElseThrow(() -> {
            throw new AuthenticationException("주문 내역을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
        });

        checkAuthOrderItem(user, orderItem);

        return orderItem;
    }



    //region PRIVATE METHOD

    private void checkAuthOrderItem(User user, OrderItem orderItem) {
        if(!orderItem.getOrder().getUser().getId().equals(user.getId())) {
            throw new AuthenticationException("접근 권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private List<OrderResponse> ordersToOrderResponses(List<Order> orders) {
        return orders.stream().map(OrderResponse::new).toList();
    }

    private List<OrderItemResponse> orderItemsToOrderItemResponse(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItemResponse::new)
                .toList();
    }

    private void checkAuthOrder(User user, Order order) {
        if(!order.getUser().equals(user)) {
            throw new AuthenticationException("권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void decreaseItemStock(OrderItem oi) {
        Item item = oi.getItem();
        Integer quantity = oi.getQuantity();
        item.increaseStock(quantity);
    }

    private Order createAndSaveOrder(User user) {
        Order order = Order.createOrder(user);
        return orderRepository.save(order);
    }

    private void createAndSaveOrder(Order order, CartItem cartItem) {
        Item item = cartItem.getItem();
        Integer quantity = cartItem.getQuantity();



        OrderItem orderItem = OrderItem.createOrderItem(order, item, quantity);
        itemService.decreaseStock(item.getId(), quantity);
        orderItemRepository.save(orderItem);
    }

    private Order getOrderDetail(Long orderId) {
        return orderRepository.findOrderFetchItems(orderId).orElseThrow(() -> {
            throw new InvalidRequest("주문 내역을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
        });
    }

    private int calculateTotalPrice(Order order) {
        return order.getOrderItems()
                .stream().mapToInt(o -> o.getTotalPrice())
                .sum();
    }

    private int calculateTotalPrice(Cart cart) {
        return cart.getCartItems().stream()
                .mapToInt(ci -> ci.getItem().getPrice() * ci.getQuantity())
                .sum();
    }

    //endregion
}
