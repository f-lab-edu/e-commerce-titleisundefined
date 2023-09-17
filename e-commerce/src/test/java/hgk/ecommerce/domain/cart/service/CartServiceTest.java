package hgk.ecommerce.domain.cart.service;

import hgk.ecommerce.domain.cart.CartItem;
import hgk.ecommerce.domain.cart.dto.request.CartItemSaveDto;
import hgk.ecommerce.domain.cart.dto.response.CartItemInfo;
import hgk.ecommerce.domain.cart.repository.CartRepository;
import hgk.ecommerce.domain.item.Item;
import hgk.ecommerce.domain.item.dto.enums.Category;
import hgk.ecommerce.domain.item.dto.request.ItemSaveDto;
import hgk.ecommerce.domain.item.dto.request.ItemSearch;
import hgk.ecommerce.domain.item.repository.ItemRepository;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.owner.dto.request.OwnerSignUpDto;
import hgk.ecommerce.domain.owner.repository.OwnerRepository;
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
import org.assertj.core.api.Assertions;
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

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class CartServiceTest {

    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    ShopRepository shopRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    CartService cartService;
    @Autowired
    ImageFileRepository imageFileRepository;
    @PersistenceContext
    EntityManager em;

    User user;
    Owner owner;
    Shop shop;
    List<Item> items;
    int itemCount = 50;

    @BeforeEach
    void beforeEach() {
        user = createUser("test-user", "test-password");
        owner = createOwner("test-owner", "test-password");
        shop = createShop("test-shop", owner);
        items = new ArrayList<>(itemCount);

        for (int i = 1; i <= itemCount; i++) {
            Item item = createItem("test-item-" + i, shop, i);
            items.add(itemRepository.save(item));
        }
        flushAndClearPersistence();
    }


    @Test
    void 정상_삽입() {
        int loopCount = 10;

        for (int i = 0; i < loopCount; i++) {
            CartItemSaveDto cartItemSaveDto = new CartItemSaveDto(items.get(i).getId(), i + 1);
            cartService.addCartItem(user, cartItemSaveDto);
        }

        flushAndClearPersistence();

        List<CartItem> cartItems = cartService.getCartItemsEntityFetchItemByCart(user);

        int size = cartItems.size();
        assertThat(size).isEqualTo(loopCount);
        for (int i = 0; i < size; i++) {
            Item item = items.get(i);
            CartItem cartItem = cartItems.get(i);
            assertThat(cartItem.getQuantity()).isEqualTo(item.getStock());
            assertThat(cartItem.getItem().getName()).isEqualTo(item.getName());
            assertThat(cartItem.getItem().getDescription()).isEqualTo(item.getDescription());
            assertThat(cartItem.getItem().getCategory()).isEqualTo(item.getCategory());
            assertThat(cartItem.getItem().getPrice()).isEqualTo(item.getPrice());
            assertThat(cartItem.getItem().getImageFile()).isNotNull();

        }
    }

    @Test
    void RESPONSE_DTO_조회() {
        int loopCount = 10;

        for (int i = 0; i < loopCount; i++) {
            CartItemSaveDto cartItemSaveDto = new CartItemSaveDto(items.get(i).getId(), i + 1);
            cartService.addCartItem(user, cartItemSaveDto);
        }

        flushAndClearPersistence();

        List<CartItemInfo> cartItemInfos = cartService.getCartItemsFetchItemByCart(user);

        for (int i = 0; i < loopCount; i++) {
            CartItemInfo cartItemInfo = cartItemInfos.get(i);
            Item item = items.get(i);

            assertThat(cartItemInfo.getCartItemId()).isNotNull();
            assertThat(cartItemInfo.getPrice()).isEqualTo(item.getPrice());
            assertThat(cartItemInfo.getQuantity()).isEqualTo(item.getStock());
            assertThat(cartItemInfo.getName()).isEqualTo(item.getName());
        }
    }

    @Test
    void 장바구니_비우기() {
        int loopCount = 10;

        for (int i = 0; i < loopCount; i++) {
            CartItemSaveDto cartItemSaveDto = new CartItemSaveDto(items.get(i).getId(), i + 1);
            cartService.addCartItem(user, cartItemSaveDto);
        }
        flushAndClearPersistence();

        List<CartItem> cartItems = cartService.getCartItemsEntityFetchItemByCart(user);
        assertThat(cartItems.size()).isEqualTo(loopCount);

        flushAndClearPersistence();
        cartService.clearCart(user);

        assertThat(cartService.getCartItemsEntityFetchItemByCart(user).size()).isEqualTo(0);
    }

    @Test
    void 카트_아이템_삭제() {
        int loopCount = 10;
        ArrayList<Long> cartItemIds = new ArrayList<>(loopCount);
        for (int i = 0; i < loopCount; i++) {
            CartItemSaveDto cartItemSaveDto = new CartItemSaveDto(items.get(i).getId(), i + 1);
            cartItemIds.add(cartService.addCartItem(user, cartItemSaveDto));
        }

        flushAndClearPersistence();

        List<CartItem> cartItems = cartService.getCartItemsEntityFetchItemByCart(user);
        assertThat(cartItems.size()).isEqualTo(loopCount);

        int deleteCount = 5;
        for (int i = 0; i < deleteCount; i++) {
            cartService.removeCartItem(user, cartItemIds.get(i));
        }

        flushAndClearPersistence();

        assertThat(cartService.getCartItemsFetchItemByCart(user).size()).isEqualTo(loopCount - deleteCount);
    }

    //region PRIVATE METHOD

    private void flushAndClearPersistence() {
        em.flush();
        em.clear();
    }

    private ItemSearch createSearchCond(String name, Category category) {
        ItemSearch itemSearch = ItemSearch.builder()
                .name(name)
                .category(category)
                .build();
        return itemSearch;
    }

    private MockMultipartFile createMockFile(String fileName) {
        return new MockMultipartFile(fileName, new byte[]{0x00});
    }

    private Owner createOwner(String loginId, String password) {
        Owner owner = Owner.createOwner(new OwnerSignUpDto(loginId, password));
        return ownerRepository.save(owner);
    }

    private Shop createShop(String shopName, Owner owner) {
        Shop shop = Shop.createShop(new ShopSaveDto(shopName), owner);
        return shopRepository.save(shop);
    }

    private User createUser(String loginId, String password) {
        UserSignUpDto userSign = UserSignUpDto.builder()
                .loginId(loginId)
                .password(password)
                .address(loginId + "'s address'")
                .nickname(loginId + "'s nick")
                .build();
        User user = User.createUser(userSign);
        return userRepository.save(user);
    }

    private Item createItem(String itemName, Shop shop, int stock) {
        ItemSaveDto itemSave = ItemSaveDto.builder()
                .itemName(itemName)
                .description(itemName + "description")
                .stock(stock)
                .price(stock)
                .category(Category.ETC)
                .file(createMockFile(itemName))
                .shopId(shop.getId())
                .build();
        ImageFile imageFile = ImageFile.createImageFile(itemName, UUID.randomUUID().toString());
        imageFileRepository.save(imageFile);

        return Item.createItem(itemSave, shop, imageFile);
    }
    //endregion
}