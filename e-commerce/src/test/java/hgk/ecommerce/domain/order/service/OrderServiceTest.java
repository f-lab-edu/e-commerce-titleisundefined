package hgk.ecommerce.domain.order.service;

import hgk.ecommerce.domain.cart.dto.request.CartItemSaveDto;
import hgk.ecommerce.domain.cart.service.CartService;
import hgk.ecommerce.domain.item.Item;
import hgk.ecommerce.domain.item.dto.enums.Category;
import hgk.ecommerce.domain.item.dto.request.ItemSaveDto;
import hgk.ecommerce.domain.item.dto.response.ItemInfo;
import hgk.ecommerce.domain.item.repository.ItemRepository;
import hgk.ecommerce.domain.order.dto.response.OrderDetail;
import hgk.ecommerce.domain.order.dto.response.OrderItemResponse;
import hgk.ecommerce.domain.order.repository.OrderRepository;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.owner.dto.request.OwnerSignUpDto;
import hgk.ecommerce.domain.owner.repository.OwnerRepository;
import hgk.ecommerce.domain.payment.service.PaymentService;
import hgk.ecommerce.domain.shop.Shop;
import hgk.ecommerce.domain.shop.dto.request.ShopSaveDto;
import hgk.ecommerce.domain.shop.repository.ShopRepository;
import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.domain.user.dto.request.UserSignUpDto;
import hgk.ecommerce.domain.user.repository.UserRepository;
import hgk.ecommerce.global.storage.ImageFile;
import hgk.ecommerce.global.storage.repository.ImageFileRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static hgk.ecommerce.domain.order.dto.enums.OrderStatus.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class OrderServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    ShopRepository shopRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ImageFileRepository imageFileRepository;
    @Autowired
    CartService cartService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderService orderService;
    @Autowired
    PaymentService paymentService;


    User user;
    Owner owner;
    Shop shop;
    List<Item> items;
    int itemCount = 10;

    @Test
    void 정상_주문() {
        User user = createUser("order-test-user", "test-password");;
        Owner owner = createOwner("order-test-owner", "test-password");;
        Shop shop = createShop("order-test-shop", owner);;
        List<Item> items = new ArrayList<>();

        for (int i = 0; i < itemCount; i++) {
            Item item = createItem("order-test-item-" + i, i, shop);
            items.add(item);
        }

        int chargePoint = 1000000;
        paymentService.increasePoint(user.getId(), chargePoint);
        int totalPrice = 0;
        for (int i = 1; i < itemCount; i++) {
            Item item = items.get(i);
            CartItemSaveDto cartItemSaveDto = new CartItemSaveDto(item.getId(), 1);
            totalPrice += cartItemSaveDto.getCount() * item.getPrice();
            cartService.addCartItem(user.getId(), cartItemSaveDto);
        }

        Long orderId = orderService.order(user.getId());
        assertThat(cartService.getCartItemsFetchItemByCart(user.getId()).size()).isEqualTo(0);
        OrderDetail orderInfo = (OrderDetail) orderService.getOrderDetail(user.getId(), orderId);

        assertThat(orderInfo.getOrderId()).isEqualTo(orderId);
        assertThat(orderInfo.getOrderStatus()).isEqualTo(ORDERED);
        List<OrderItemResponse> orderItems = orderInfo.getOrderItems();

        for (int i = 0; i < orderItems.size(); i++) {
            Item item = items.get(i + 1);
            OrderItemResponse orderItemResponse = orderItems.get(i);

            assertThat(orderItemResponse.getOrderItemId()).isNotNull();
            assertThat(orderItemResponse.getItemId()).isNotNull();
            assertThat(orderItemResponse.getItemName()).isEqualTo(item.getName());
            assertThat(orderItemResponse.getQuantity()).isEqualTo(1);
            assertThat(orderItemResponse.getTotalPrice()).isEqualTo(item.getPrice());
        }
    }

    @Test
    void 주문_취소() {
        User user = createUser("cancel-test-user", "test-password");;
        Owner owner = createOwner("cancel-test-owner", "test-password");;
        Shop shop = createShop("cancel-test-shop", owner);;
        List<Item> items = new ArrayList<>();

        for (int i = 0; i < itemCount; i++) {
            Item item = createItem("cancel-test-item-" + i, i, shop);
            items.add(item);
        }

        int chargePoint = 1000000;
        paymentService.increasePoint(user.getId(), chargePoint);

        for (int i = 1; i < itemCount; i++) {
            Item item = items.get(i);
            CartItemSaveDto cartItemSaveDto = new CartItemSaveDto(item.getId(), 1);
            cartService.addCartItem(user.getId(), cartItemSaveDto);
        }

        Long orderId = orderService.order(user.getId());

        for (int i = 1; i < itemCount; i++) {
            Item item = itemRepository.findById(items.get(i).getId()).get();

            assertThat(item.getStock()).isEqualTo(items.get(i).getStock() - 1);
        }

        orderService.cancelOrder(user.getId(), orderId);

        for (int i = 1; i < itemCount; i++) {
            Item item = itemRepository.findById(items.get(i).getId()).get();

            assertThat(item.getStock()).isEqualTo(items.get(i).getStock());
        }
    }


    private User createUser(String loginId, String password) {
        UserSignUpDto userSign = UserSignUpDto.builder()
                .loginId(loginId)
                .password(password)
                .address(loginId + "s address")
                .nickname(loginId + "s nick")
                .build();
        User user = User.createUser(userSign);
        return userRepository.save(user);
    }

    private Owner createOwner(String loginId, String password) {
        OwnerSignUpDto ownerSign = OwnerSignUpDto.builder()
                .loginId(loginId)
                .password(password)
                .build();
        Owner owner = Owner.createOwner(ownerSign);
        return ownerRepository.save(owner);
    }

    private Shop createShop(String shopName, Owner owner) {
        ShopSaveDto shopSave = new ShopSaveDto(shopName);
        Shop shop = Shop.createShop(shopSave, owner);
        return shopRepository.save(shop);
    }

    private Item createItem(String itemName, int i, Shop shop) {
        ItemSaveDto itemSave = ItemSaveDto.builder()
                .itemName(itemName)
                .description(itemName)
                .stock(i)
                .price(i)
                .category(Category.TEST)
                .shopId(shop.getId())
                .file(new MockMultipartFile(itemName, new byte[]{0x00}))
                .build();
        ImageFile imageFile = ImageFile.createImageFile(itemName, UUID.randomUUID().toString());
        imageFileRepository.save(imageFile);

        Item item = Item.createItem(itemSave, shop, imageFile);
        return itemRepository.save(item);
    }

}