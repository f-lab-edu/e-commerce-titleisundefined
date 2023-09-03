package hgk.ecommerce.domain.cart.service;

import hgk.ecommerce.domain.cart.Cart;
import hgk.ecommerce.domain.cart.CartItem;
import hgk.ecommerce.domain.cart.dto.CartItemResponse;
import hgk.ecommerce.domain.cart.dto.CartItemSave;
import hgk.ecommerce.domain.cart.repository.CartItemRepository;
import hgk.ecommerce.domain.item.Item;
import hgk.ecommerce.domain.item.dto.Category;
import hgk.ecommerce.domain.item.dto.ItemSave;
import hgk.ecommerce.domain.item.repository.ItemRepository;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.owner.dto.OwnerSign;
import hgk.ecommerce.domain.owner.repository.OwnerRepository;
import hgk.ecommerce.domain.shop.Shop;
import hgk.ecommerce.domain.shop.dto.request.ShopSave;
import hgk.ecommerce.domain.shop.repository.ShopRepository;
import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.domain.user.dto.request.UserSign;
import hgk.ecommerce.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static hgk.ecommerce.domain.item.dto.Category.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class CartServiceTest {
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
    @PersistenceContext
    EntityManager em;

    User user;
    Owner owner;
    Shop shop;
    List<Item> items = new ArrayList<>();

    @BeforeEach
    void beforeEach() {
        user = createUser("user-id", "user-password");
        owner = createOwner("test-owner", "test-password");
        shop = createShop(owner, "test-shop-name");
        items.add(createItem(shop, "test-item1"));
        items.add(createItem(shop, "test-item2"));
        items.add(createItem(shop, "test-item3"));
        items.add(createItem(shop, "test-item4"));

        em.flush();
        em.clear();
    }

    //region 장바구니 조회

    @Test
    void 장바구니_조회() {
        Cart cart = cartService.getCart(user);

        assertThat(cart.getCartItems().size()).isEqualTo(0);
        assertThat(cart.getUser()).isEqualTo(user);

        em.flush();
        em.clear();

        for (int i = 1; i <= items.size(); i++) {
            Item item = items.get(i - 1);
            CartItemSave cartItemSave = new CartItemSave(item.getId(), 1);
            cartService.addToCart(user, cartItemSave);
        }

        em.flush();
        em.clear();

        List<CartItemResponse> cartItemResponses = cartService.getCartItemResponses(user);

        assertThat(cartItemResponses.size()).isEqualTo(items.size());
    }

    //endregion

    //region 장바구니 삭제
    @Test
    void 장바구니_삭제() {
        Item item = items.get(0);

        CartItemSave cartItemSave = new CartItemSave(item.getId(), 1);
        cartService.addToCart(user, cartItemSave);

        em.flush();
        em.clear();

        List<CartItemResponse> cartItemResponses = cartService.getCartItemResponses(user);
        assertThat(cartItemResponses.size()).isEqualTo(1);


        CartItemResponse cartItemResponse = cartItemResponses.get(0);

        cartService.removeCart(user, cartItemResponse.getCartItemId());
        em.flush();
        em.clear();

        assertThat(cartService.getCartItemResponses(user).size()).isEqualTo(0);
    }
    //endregion

    //region 장바구니 추가
    @Test
    void 장바구니_추가() {
        ArrayList<CartItemSave> cartItemSaves = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            CartItemSave cartItemSave = new CartItemSave(item.getId(), i + 1);
            cartItemSaves.add(cartItemSave);
            cartService.addToCart(user, cartItemSave);
        }

        em.flush();
        em.clear();

        List<CartItemResponse> cartItemResponses = cartService.getCartItemResponses(user);
        assertThat(cartItemResponses.size()).isEqualTo(items.size());
    }

    @Test
    void 중복_아이템_추가() {
        Item item = items.get(0);
        int loopCount = 10;

        for (int i = 0; i < loopCount; i++) {
            CartItemSave cartItemSave = new CartItemSave(item.getId(), 1);
            cartService.addToCart(user, cartItemSave);
        }

        em.flush();
        em.clear();

        List<CartItemResponse> cartItemResponses = cartService.getCartItemResponses(user);

        assertThat(cartItemResponses.size()).isEqualTo(1);
        CartItemResponse cartItemResponse = cartItemResponses.get(0);
        assertThat(cartItemResponse.getQuantity()).isEqualTo(loopCount);
    }

    //endregion

    //region PRIVATE METHOD

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
        ItemSave itemSave = new ItemSave(itemName, 100000, 10000, ALBUM, shop.getId());
        Item item = Item.createItem(itemSave, shop);
        return itemRepository.save(item);
    }

    //endregion
}