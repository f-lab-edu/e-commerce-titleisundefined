package hgk.ecommerce.domain.item.service;

import hgk.ecommerce.domain.common.exceptions.AuthenticationException;
import hgk.ecommerce.domain.item.Item;
import hgk.ecommerce.domain.item.dto.enums.Category;
import hgk.ecommerce.domain.item.dto.request.ItemEditDto;
import hgk.ecommerce.domain.item.dto.request.ItemFileDto;
import hgk.ecommerce.domain.item.dto.request.ItemSaveDto;
import hgk.ecommerce.domain.item.dto.request.ItemSearch;
import hgk.ecommerce.domain.item.dto.response.ItemInfo;
import hgk.ecommerce.domain.item.repository.ItemRepository;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.owner.dto.request.OwnerSignUpDto;
import hgk.ecommerce.domain.owner.repository.OwnerRepository;
import hgk.ecommerce.domain.shop.Shop;
import hgk.ecommerce.domain.shop.dto.request.ShopSaveDto;
import hgk.ecommerce.domain.shop.repository.ShopRepository;
import hgk.ecommerce.global.storage.ImageFile;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static hgk.ecommerce.domain.item.dto.enums.ItemStatus.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceTest {
    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    ShopRepository shopRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemService itemService;
    @PersistenceContext
    EntityManager em;

    Owner ownerA;
    Owner ownerB;
    Shop shop;

    @BeforeEach
    void beforeEach() {
        ownerA = createOwner("test-loginidA", "test-password");
        ownerB = createOwner("test-loginidB", "test-password");
        shop = createShop("test-shop", ownerA);
    }

    //region 아이템 등록

    @Test
    void 정상_등록() {
        ItemSaveDto saveDto = ItemSaveDto.builder()
                .itemName("test-name")
                .shopId(shop.getId())
                .stock(10000)
                .price(1000000)
                .file(createMockFile("test.jpg"))
                .category(Category.ETC)
                .description("test-description")
                .build();

        itemService.enrollItem(ownerA, saveDto);
        flushAndClearPersistence();

        List<ItemInfo> itemInfos = itemService.getItemsByShop(ownerA, shop.getId(), 1, 5);

        assertThat(itemInfos.size()).isEqualTo(1);

        ItemInfo itemInfo = itemInfos.get(0);

        assertThat(itemInfo.getItemId()).isNotNull();
        assertThat(itemInfo.getName()).isEqualTo(saveDto.getItemName());
        assertThat(itemInfo.getPrice()).isEqualTo(saveDto.getPrice());
        assertThat(itemInfo.getDescription()).isEqualTo(saveDto.getDescription());
        assertThat(itemInfo.getStock()).isEqualTo(saveDto.getStock());
        assertThat(itemInfo.getCategory()).isEqualTo(saveDto.getCategory());
        assertThat(itemInfo.getImageLink()).isNotNull();
        assertThat(itemInfo.getStatus()).isEqualTo(ACTIVE);
    }

    @Test
    void 타인가게_등록() {
        ItemSaveDto saveDto = ItemSaveDto.builder()
                .itemName("test-name")
                .shopId(shop.getId())
                .stock(10000)
                .price(1000000)
                .file(createMockFile("test.jpg"))
                .category(Category.ETC)
                .description("test-description")
                .build();

        assertThatThrownBy(() -> itemService.enrollItem(ownerB, saveDto))
                .isInstanceOf(AuthenticationException.class);
    }

    //endregion

    //region 아이템 조회

    @Test
    void 검색어_조회() {
        int loopCount = 5;
        for (int i = 0; i < loopCount; i++) {
            ItemSaveDto saveDto = ItemSaveDto.builder()
                    .itemName("test-name" + i)
                    .shopId(shop.getId())
                    .stock(i)
                    .price(1000 * i)
                    .file(createMockFile("test.jpg"))
                    .category(Category.ETC)
                    .description("test-description")
                    .build();

            itemService.enrollItem(ownerA, saveDto);
        }

        flushAndClearPersistence();

        ItemSearch itemSearch = createSearchCond("test-name", null);

        assertThat(itemService.searchItems(itemSearch, 1, loopCount).size()).isEqualTo(loopCount);
        assertThat(itemService.searchItems(itemSearch, 1, loopCount + 1).size()).isEqualTo(loopCount);
        assertThat(itemService.searchItems(itemSearch, 1, loopCount - 1).size()).isEqualTo(loopCount - 1);
    }

    @Test
    void 카테고리_조회() {
        int loopCount = 5;

        for (int i = 0; i < loopCount; i++) {
            ItemSaveDto saveDto = ItemSaveDto.builder()
                    .itemName("test-name" + i)
                    .shopId(shop.getId())
                    .stock(i)
                    .price(1000 * i)
                    .file(createMockFile("test.jpg"))
                    .category(Category.ETC)
                    .description("test-description")
                    .build();

            itemService.enrollItem(ownerA, saveDto);
        }

        for (int i = 0; i < loopCount; i++) {
            ItemSaveDto saveDto = ItemSaveDto.builder()
                    .itemName("test-name" + i)
                    .shopId(shop.getId())
                    .stock(i)
                    .price(1000 * i)
                    .file(createMockFile("test.jpg"))
                    .category(Category.BOOK)
                    .description("test-description")
                    .build();

            itemService.enrollItem(ownerA, saveDto);
        }

        for (int i = 0; i < loopCount; i++) {
            ItemSaveDto saveDto = ItemSaveDto.builder()
                    .itemName("test-name" + i)
                    .shopId(shop.getId())
                    .stock(i)
                    .price(1000 * i)
                    .file(createMockFile("test.jpg"))
                    .category(Category.ALBUM)
                    .description("test-description")
                    .build();

            itemService.enrollItem(ownerA, saveDto);
        }

        for (int i = 0; i < loopCount; i++) {
            ItemSaveDto saveDto = ItemSaveDto.builder()
                    .itemName("test-name" + i)
                    .shopId(shop.getId())
                    .stock(i)
                    .price(1000 * i)
                    .file(createMockFile("test.jpg"))
                    .category(Category.CLOTHES)
                    .description("test-description")
                    .build();

            itemService.enrollItem(ownerA, saveDto);
        }

        flushAndClearPersistence();

        assertThat(itemService.searchItems(createSearchCond(null, Category.ALBUM), 1, 10).size()).isEqualTo(loopCount);
        assertThat(itemService.searchItems(createSearchCond(null, Category.FOOD), 1, 10).size()).isEqualTo(0);
        assertThat(itemService.searchItems(createSearchCond(null, Category.ETC), 1, 10).size()).isEqualTo(loopCount);
        assertThat(itemService.searchItems(createSearchCond(null, Category.CLOTHES), 1, 10).size()).isEqualTo(loopCount);
        assertThat(itemService.searchItems(createSearchCond(null, Category.BOOK), 1, 10).size()).isEqualTo(loopCount);
    }

    @Test
    void 검색어_카테고리_조회() {
        int loopCount = 10;
        for (int i = 0; i < loopCount; i++) {
            ItemSaveDto saveDto = ItemSaveDto.builder()
                    .itemName("ETC" + i)
                    .shopId(shop.getId())
                    .stock(i)
                    .price(1000 * i)
                    .file(createMockFile("test.jpg"))
                    .category(Category.ETC)
                    .description("test-description")
                    .build();

            itemService.enrollItem(ownerA, saveDto);
        }

        assertThat(itemService.searchItems(createSearchCond("ETC", Category.ETC), 1, loopCount).size()).isEqualTo(loopCount);
        assertThat(itemService.searchItems(createSearchCond("ETC", Category.ALBUM), 1, loopCount).size()).isEqualTo(0);
        assertThat(itemService.searchItems(createSearchCond("ALBUM", Category.ETC), 1, loopCount).size()).isEqualTo(0);
    }

    @Test
    void 가게로_아이템_조회() {
        int loopCount = 100;
        for (int i = 0; i < loopCount; i++) {
            ItemSaveDto saveDto = ItemSaveDto.builder()
                    .itemName("test-name" + i)
                    .shopId(shop.getId())
                    .stock(i)
                    .price(1000 * i)
                    .file(createMockFile("test.jpg"))
                    .category(Category.ETC)
                    .description("test-description")
                    .build();

            itemService.enrollItem(ownerA, saveDto);
        }

        assertThat(itemService.getItemsByShop(ownerA, shop.getId(), 1, loopCount).size()).isEqualTo(loopCount);
        assertThat(itemService.getItemsByShop(ownerA, shop.getId(), 1, loopCount + 1).size()).isEqualTo(loopCount);
        assertThat(itemService.getItemsByShop(ownerA, shop.getId(), 1, loopCount - 1).size()).isEqualTo(loopCount - 1);
    }

    @Test
    void 타인_가게_아이템_조회() {
        int loopCount = 10;
        for (int i = 0; i < loopCount; i++) {
            ItemSaveDto saveDto = ItemSaveDto.builder()
                    .itemName("test-name" + i)
                    .shopId(shop.getId())
                    .stock(i)
                    .price(1000 * i)
                    .file(createMockFile("test.jpg"))
                    .category(Category.ETC)
                    .description("test-description")
                    .build();

            itemService.enrollItem(ownerA, saveDto);
        }

        assertThatThrownBy(() -> itemService.getItemsByShop(ownerB, shop.getId(), 1, loopCount))
                .isInstanceOf(AuthenticationException.class);
    }

    //endregion

    //region 아이템 수정

    @Test
    void 정상_수정() {
        ItemSaveDto saveDto = ItemSaveDto.builder()
                .itemName("test-name")
                .shopId(shop.getId())
                .stock(10000)
                .price(1000000)
                .file(createMockFile("test.jpg"))
                .category(Category.ETC)
                .description("test-description")
                .build();

        Long itemId = itemService.enrollItem(ownerA, saveDto);
        flushAndClearPersistence();

        ItemEditDto itemEditDto = ItemEditDto.builder()
                .itemId(itemId)
                .itemStatus(SUSPEND)
                .itemName("change-name")
                .stock(1)
                .price(1)
                .category(Category.ALBUM)
                .description("change-description")
                .build();

        itemService.editItem(ownerA, itemEditDto);
        flushAndClearPersistence();

        Item item = itemRepository.findById(itemId).get();

        assertThat(item.getShop().getId()).isEqualTo(saveDto.getShopId());
        assertThat(item.getName()).isEqualTo(itemEditDto.getItemName());
        assertThat(item.getStatus()).isEqualTo(itemEditDto.getItemStatus());
        assertThat(item.getStock()).isEqualTo(itemEditDto.getStock());
        assertThat(item.getPrice()).isEqualTo(itemEditDto.getPrice());
        assertThat(item.getDescription()).isEqualTo(itemEditDto.getDescription());
    }

    @Test
    void 타인_아이템_수정() {
        ItemSaveDto saveDto = ItemSaveDto.builder()
                .itemName("test-name")
                .shopId(shop.getId())
                .stock(10000)
                .price(1000000)
                .file(createMockFile("test.jpg"))
                .category(Category.ETC)
                .description("test-description")
                .build();

        Long itemId = itemService.enrollItem(ownerA, saveDto);
        flushAndClearPersistence();

        ItemEditDto itemEditDto = ItemEditDto.builder()
                .itemId(itemId)
                .itemStatus(SUSPEND)
                .itemName("change-name")
                .stock(1)
                .price(1)
                .category(Category.ALBUM)
                .description("change-description")
                .build();

        assertThatThrownBy(() -> itemService.editItem(ownerB, itemEditDto))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void 이미지파일_수정() {
        ItemSaveDto saveDto = ItemSaveDto.builder()
                .itemName("test-name")
                .shopId(shop.getId())
                .stock(10000)
                .price(1000000)
                .file(createMockFile("test.jpg"))
                .category(Category.ETC)
                .description("test-description")
                .build();

        Long itemId = itemService.enrollItem(ownerA, saveDto);
        flushAndClearPersistence();

        ItemFileDto itemFileDto = ItemFileDto.builder()
                .file(createMockFile("change-file.jpg"))
                .itemId(itemId)
                .build();
        Item item = itemService.getItemEntity(itemId);
        String virtualName = item.getImageFile().getVirtualName();

        itemService.changeItemImage(ownerA, itemFileDto);

        flushAndClearPersistence();

        item = itemService.getItemEntity(itemId);
        ImageFile imageFile = item.getImageFile();

        assertThat(imageFile.getOriginalName()).isEqualTo(itemFileDto.getFile().getName());
        assertThat(imageFile.getVirtualName()).isNotEqualTo(virtualName);
    }
    //endregion

    //region 아이템 동시성 재고
    @SpringBootTest
    @ActiveProfiles("test")
    static class 재고증가테스트 {
        @Autowired
        OwnerRepository ownerRepository;
        @Autowired
        ShopRepository shopRepository;
        @Autowired
        ItemRepository itemRepository;
        @Autowired
        ItemService itemService;


        @Test
        void 재고_증가_동시성() throws InterruptedException {
            Owner ownerA = createOwner("test-loginid-increase", "test-password");
            Shop shop = createShop("test-shop-increase", ownerA);

            ItemSaveDto saveDto = ItemSaveDto.builder()
                    .itemName("mock-increase-item")
                    .shopId(shop.getId())
                    .stock(0)
                    .price(1000000)
                    .file(createMockFile("test.jpg"))
                    .category(Category.TEST)
                    .description("test-description")
                    .build();

            Long itemId = itemService.enrollItem(ownerA, saveDto);

            int threadCount = 100;

            ExecutorService executorService = Executors.newFixedThreadPool(32);
            CountDownLatch latch = new CountDownLatch(threadCount);

            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                            try {
                                itemService.increaseStock(itemId, 1);
                            } finally {
                                latch.countDown();
                            }
                        }
                );
            }

            latch.await();


            Item item = itemService.getItemEntity(itemId);
            assertThat(item.getStock()).isEqualTo(threadCount);

        }

        @Test
        void 재고_감소_동시성() throws InterruptedException {
            Owner ownerA = createOwner("test-loginid-decrease", "test-password");
            Shop shop = createShop("test-shop-decrease", ownerA);

            ItemSaveDto saveDto = ItemSaveDto.builder()
                    .itemName("mock-decrease-item")
                    .shopId(shop.getId())
                    .stock(100)
                    .price(1000000)
                    .file(createMockFile("test.jpg"))
                    .category(Category.TEST)
                    .description("test-description")
                    .build();

            Long itemId = itemService.enrollItem(ownerA, saveDto);

            int threadCount = 100;

            ExecutorService executorService = Executors.newFixedThreadPool(32);
            CountDownLatch latch = new CountDownLatch(threadCount);

            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                            try {
                                itemService.decreaseStock(itemId, 1);
                            } finally {
                                latch.countDown();
                            }
                        }
                );
            }

            latch.await();


            Item item = itemService.getItemEntity(itemId);
            assertThat(item.getStock()).isEqualTo(0);
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
    }
    //endregion

    //region Stub Method

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

    //endregion
}