package hgk.ecommerce.domain.order.service;

import hgk.ecommerce.domain.cart.Cart;
import hgk.ecommerce.domain.cart.CartItem;
import hgk.ecommerce.domain.cart.dto.CartItemSave;
import hgk.ecommerce.domain.cart.service.CartService;
import hgk.ecommerce.domain.common.exception.InvalidRequest;
import hgk.ecommerce.domain.common.exception.NoResourceException;
import hgk.ecommerce.domain.item.Item;
import hgk.ecommerce.domain.item.dto.ItemEdit;
import hgk.ecommerce.domain.item.dto.ItemSave;
import hgk.ecommerce.domain.item.dto.ItemStatus;
import hgk.ecommerce.domain.item.repository.ItemRepository;
import hgk.ecommerce.domain.order.dto.OrderItemResponse;
import hgk.ecommerce.domain.order.dto.OrderResponse;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.owner.dto.OwnerSign;
import hgk.ecommerce.domain.owner.repository.OwnerRepository;
import hgk.ecommerce.domain.payment.Payment;
import hgk.ecommerce.domain.payment.service.PaymentService;
import hgk.ecommerce.domain.shop.Shop;
import hgk.ecommerce.domain.shop.dto.request.ShopSave;
import hgk.ecommerce.domain.shop.repository.ShopRepository;
import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.domain.user.dto.request.UserSign;
import hgk.ecommerce.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static hgk.ecommerce.domain.item.dto.Category.ALBUM;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    OrderService orderService;
    @Autowired
    CartService cartService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ShopRepository shopRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    PaymentService paymentService;
    @PersistenceContext
    EntityManager em;

    User userA;
    User userB;
    Owner owner;
    Shop shop;
    List<Item> items = new ArrayList<>();

    @BeforeEach
    void beforeEach() {
        userA = createUser("user-id-1", "user-password");
        userB = createUser("user-id-2", "user-password");
        owner = createOwner("test-owner", "test-password");
        shop = createShop(owner, "test-shop-name");
        items.add(createItem(shop, "test-item1"));
        items.add(createItem(shop, "test-item2"));
        items.add(createItem(shop, "test-item3"));
        items.add(createItem(shop, "test-item4"));

        for (int i = 0; i < items.size(); i++) {
            addCart(userA, items.get(i), i + 1);
        }

        em.flush();
        em.clear();
    }


    //region 주문

    @Test
    void 정상_주문() {
        int chargePoint = 100000;
        paymentService.chargePoint(userA, chargePoint);
        orderService.createOrder(userA);

        Cart cart = cartService.getCart(userA);
        List<CartItem> cartItems = cartService.getCartItemsFetchItemsByCart(cart);
        int totalPrice = cartItems.stream().mapToInt(a -> a.getItem().getPrice() * a.getQuantity()).sum();

        em.flush();
        em.clear();

        Payment payment = paymentService.getPaymentByUser(userA);

        // 포인트 확인
        assertThat(payment.getPoint()).isEqualTo(chargePoint - totalPrice);

        // 재고확인
        for (int i = 0; i < cartItems.size(); i++) {
            Item item = cartItems.get(i).getItem();
            assertThat(item.getStock()).isEqualTo(100 - (i + 1));
        }
    }

    @Test
    void 포인트_부족_주문() {

        Cart cart = cartService.getCart(userA);
        List<CartItem> cartItems = cartService.getCartItemsFetchItemsByCart(cart);
        int totalPrice = cartItems.stream().mapToInt(a -> a.getItem().getPrice() * a.getQuantity()).sum();

        int chargePoint = totalPrice - 1;
        paymentService.chargePoint(userA, chargePoint);

        em.flush();
        em.clear();

        assertThatThrownBy(() -> orderService.createOrder(userA))
                .isInstanceOf(NoResourceException.class);

        // 재고확인
        for (CartItem cartItem : cartItems) {
            Item item = itemRepository.findById(cartItem.getItem().getId()).orElseThrow();
            assertThat(item.getStock()).isEqualTo(100);
        }
    }

    @Test
    void 판매중단_아이템_주문() {
        int chargePoint = 100000;
        paymentService.chargePoint(userA, chargePoint);
        orderService.createOrder(userA);

        Item targetItem = items.get(0);
        ItemEdit itemEdit = new ItemEdit(targetItem.getName(), targetItem.getStock(), targetItem.getPrice(), ItemStatus.DELETED, targetItem.getCategory());
        itemRepository.findById(targetItem.getId()).get().editItem(itemEdit);

        em.flush();
        em.clear();

        assertThatThrownBy(() -> orderService.createOrder(userA))
                .isInstanceOf(InvalidRequest.class);
    }

    //endregion

    //region 주문 취소

    @Test
    void 정상_주문_취소() {
        int chargePoint = 100000;
        paymentService.chargePoint(userA, chargePoint);
        orderService.createOrder(userA);

        Cart cart = cartService.getCart(userA);
        List<CartItem> cartItems = cartService.getCartItemsFetchItemsByCart(cart);
        int totalPrice = cartItems.stream().mapToInt(a -> a.getItem().getPrice() * a.getQuantity()).sum();

        em.flush();
        em.clear();

        List<OrderResponse> orderResponses = orderService.getOrders(userA, 1, 5);
        assertThat(orderResponses.size()).isEqualTo(1);

        OrderResponse orderResponse = orderResponses.get(0);
        orderService.cancelOrder(userA, orderResponse.getOrderId());

        em.flush();
        em.clear();

        Payment payment = paymentService.getPaymentByUser(userA);

        assertThat(payment.getPoint()).isEqualTo(chargePoint);

        for (Item item : items) {
            Item foundItem = itemRepository.findById(item.getId()).orElseThrow();
            assertThat(foundItem.getStock()).isEqualTo(100);
        }
    }


    //endregion


    //region PRIVATE METHOD

    private void addCart(User user, Item item, int quantity) {
        CartItemSave cartItemSave = new CartItemSave(item.getId(), quantity);
        cartService.addToCart(user, cartItemSave);
    }

    private User createUser(String loginId, String password) {
        UserSign userSign = new UserSign(loginId, password, loginId, "address");
        User user = User.createUser(userSign);
        return userRepository.save(user);
    }

    private Owner createOwner(String loginId, String password) {
        OwnerSign ownerSign = new OwnerSign(loginId, password);
        Owner owner = Owner.createOwner(ownerSign);
        return ownerRepository.save(owner);
    }

    private Shop createShop(Owner owner, String shopName) {
        ShopSave shopSave = new ShopSave(shopName);
        Shop shop = Shop.createShop(shopSave, owner);
        return shopRepository.save(shop);
    }

    private Item createItem(Shop shop, String itemName) {
        ItemSave itemSave = new ItemSave(itemName, 100, 1000, ALBUM, shop.getId());
        Item item = Item.createItem(itemSave, shop);
        return itemRepository.save(item);
    }

    //endregion
}