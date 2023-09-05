package hgk.ecommerce.domain.item.service;

import hgk.ecommerce.domain.common.exception.AuthenticationException;
import hgk.ecommerce.domain.common.exception.NoResourceException;
import hgk.ecommerce.domain.item.Item;
import hgk.ecommerce.domain.item.dto.*;
import hgk.ecommerce.domain.item.repository.ItemRepository;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.owner.dto.OwnerSign;
import hgk.ecommerce.domain.owner.repository.OwnerRepository;
import hgk.ecommerce.domain.shop.Shop;
import hgk.ecommerce.domain.shop.dto.request.ShopSave;
import hgk.ecommerce.domain.shop.repository.ShopRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static hgk.ecommerce.domain.item.dto.ItemStatus.*;
import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class ItemServiceTest {
    @Autowired
    ItemService itemService;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    ShopRepository shopRepository;
    @Autowired
    PlatformTransactionManager transactionManager;
    @PersistenceContext
    EntityManager em;

    Shop shopA;
    Owner ownerA;
    Owner ownerB;

    @BeforeEach
    void beforeEach() {
        String loginIdA = "owner-id-A";
        String loginIdB = "owner-id-B";
        String password = "test-password";
        this.ownerA = createOwner(loginIdA, password);
        this.ownerB = createOwner(loginIdB, password);

        this.shopA = createShop("test-shop", ownerA);

    }

    //region 상품 등록
    @Test
    void 정상_등록() {
        Category category = Category.ELECTRONIC;
        int price = 100000;
        int stock = 10;
        String itemName = "test-item";

        ItemSave itemSave = new ItemSave(itemName, stock, price, category, shopA.getId());
        Long itemId = itemService.addItem(ownerA, itemSave);

        em.flush();
        em.clear();

        Item item = itemRepository.findById(itemId).orElseThrow();

        assertThat(item.getPrice()).isEqualTo(price);
        assertThat(item.getShop().getId()).isEqualTo(shopA.getId());
        assertThat(item.getStock()).isEqualTo(stock);
        assertThat(item.getCategory()).isEqualTo(category);
        assertThat(item.getStatus()).isEqualTo(ACTIVE);
    }

    @Test
    void 타인가게_등록() {
        Category category = Category.ELECTRONIC;
        int price = 100000;
        int stock = 10;
        String itemName = "test-item";

        ItemSave itemSave = new ItemSave(itemName, stock, price, category, shopA.getId());
        assertThatThrownBy(() -> itemService.addItem(ownerB, itemSave))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void 존재하지않는_가게_등록() {
        Category category = Category.ELECTRONIC;
        int price = 100000;
        int stock = 10;
        String itemName = "test-item";

        ItemSave itemSave = new ItemSave(itemName, stock, price, category, 100L);
        assertThatThrownBy(() -> itemService.addItem(ownerA, itemSave))
                .isInstanceOf(NoResourceException.class);
    }
    //endregion

    //region 상품 조회
    @Test
    void 카테고리_조회() {
        int loop = 10;
        ArrayList<ItemSave> itemSaves = new ArrayList<>();
        ItemSearchCond searchCond = new ItemSearchCond(null, Category.ELECTRONIC);
        int before = itemService.getItems(searchCond, 1, 100).size();
        for (int i = 0; i < loop; i++) {
            ItemSave itemSave = new ItemSave("item-" + i, i, i, Category.ELECTRONIC, shopA.getId());
            itemService.addItem(ownerA, itemSave);
            itemSaves.add(itemSave);
        }
        em.flush();
        em.clear();


        assertThat(itemService.getItems(searchCond, 1, 100).size()).isEqualTo(before + loop);
    }

    @Test
    void 텍스트_조회() {
        int loop = 10;
        ArrayList<ItemSave> itemSaves = new ArrayList<>();
        for (int i = 0; i < loop; i++) {
            ItemSave itemSave = new ItemSave("item-" + i, i, i, Category.ELECTRONIC, shopA.getId());
            itemService.addItem(ownerA, itemSave);
            itemSaves.add(itemSave);
        }
        em.flush();
        em.clear();

        ItemSearchCond searchCond1 = new ItemSearchCond("item", null);
        assertThat(itemService.getItems(searchCond1, 1, 5).size()).isEqualTo(5);
        assertThat(itemService.getItems(searchCond1, 1, 10).size()).isEqualTo(10);
        assertThat(itemService.getItems(searchCond1, 1, 11).size()).isEqualTo(10);
    }

    @Test
    void 테스트_카테고리_동시조회() {
        int loop = 10;
        ArrayList<ItemSave> itemSaves = new ArrayList<>();
        for (int i = 0; i < loop; i++) {
            ItemSave itemSave = new ItemSave("item-" + i, i, i, Category.ELECTRONIC, shopA.getId());
            itemService.addItem(ownerA, itemSave);
            itemSaves.add(itemSave);
        }
        em.flush();
        em.clear();

        ItemSearchCond searchCond1 = new ItemSearchCond("item", Category.ELECTRONIC);
        assertThat(itemService.getItems(searchCond1, 1, 5).size()).isEqualTo(5);
        assertThat(itemService.getItems(searchCond1, 1, 10).size()).isEqualTo(10);
        assertThat(itemService.getItems(searchCond1, 1, 11).size()).isEqualTo(10);

        ItemSearchCond searchCond2 = new ItemSearchCond("itemd", Category.ELECTRONIC);
        assertThat(itemService.getItems(searchCond2, 1, 5).size()).isEqualTo(0);

        ItemSearchCond searchCond3 = new ItemSearchCond("item", Category.ETC);
        assertThat(itemService.getItems(searchCond3, 1, 5).size()).isEqualTo(0);

    }
    //endregion

    //region 상품 수정
    @Test
    void 정상_수정() {
        Category category = Category.ELECTRONIC;
        int price = 100000;
        int stock = 10;
        String itemName = "test-item";

        ItemSave itemSave = new ItemSave(itemName, stock, price, category, shopA.getId());
        Long itemId = itemService.addItem(ownerA, itemSave);

        em.flush();
        em.clear();

        ItemEdit itemEdit = new ItemEdit("test-item2", stock + 10, price + 10000, DELETED, Category.FOOD);
        itemService.editItem(ownerA, itemId, itemEdit);

        em.flush();
        em.clear();

        Item item = itemService.getItemEntity(itemId);
        assertThat(itemEdit.getName()).isEqualTo(item.getName());
        assertThat(itemEdit.getStatus()).isEqualTo(item.getStatus());
        assertThat(itemEdit.getStock()).isEqualTo(item.getStock());
        assertThat(itemEdit.getCategory()).isEqualTo(item.getCategory());
        assertThat(itemEdit.getPrice()).isEqualTo(item.getPrice());
    }

    @Test
    void 타인_아이템_수정() {
        Category category = Category.ELECTRONIC;
        int price = 100000;
        int stock = 10;
        String itemName = "test-item";

        ItemSave itemSave = new ItemSave(itemName, stock, price, category, shopA.getId());
        Long itemId = itemService.addItem(ownerA, itemSave);

        em.flush();
        em.clear();

        ItemEdit itemEdit = new ItemEdit("test-item2", stock + 10, price + 10000, DELETED, Category.FOOD);
        assertThatThrownBy(() -> itemService.editItem(ownerB, itemId, itemEdit))
                .isInstanceOf(AuthenticationException.class);

    }
    //endregion

    //region [격리] 재고 동시성 테스트
    @SpringBootTest
    static class IncreaseStockTest {
        @Autowired
        ShopRepository shopRepository;
        @Autowired
        ItemRepository itemRepository;
        @Autowired
        ItemService itemService;
        @Autowired
        OwnerRepository ownerRepository;

        @Test
        @Transactional(propagation = Propagation.NOT_SUPPORTED)
        void 재고_동시성_테스트() throws InterruptedException {
            Owner owner = createOwner("test-owner-concurrent-1", "test-password");
            Shop shop = createShop("test-concurrent-1", owner);

            Category category = Category.ELECTRONIC;
            int price = 100000;
            int stock = 1000;
            String itemName = "test-item";

            ItemSave itemSave = new ItemSave(itemName, stock, price, category, shop.getId());
            Long itemId = itemService.addItem(owner, itemSave);

            int threadCount = 100;
            ExecutorService executorService = Executors.newFixedThreadPool(16); // ThreadPool 구성
            CountDownLatch latch = new CountDownLatch(threadCount); // 다른 스레드에서 작업이 완료될 때까지 대기

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

            assertThat(item.getStock()).isEqualTo(stock + threadCount);
        }

        //region PRIVATE METHOD
        private Shop createShop(String shopName, Owner owner) {
            Shop shop = Shop.createShop(new ShopSave(shopName), owner);
            return shopRepository.save(shop);
        }

        private Owner createOwner(String loginId, String password) {
            OwnerSign ownerSignA = new OwnerSign(loginId, password);
            Owner owner = Owner.createOwner(ownerSignA);
            return ownerRepository.save(owner);
        }
        //endregion
    }

    @SpringBootTest
    static class DecreaseStockTest {
        @Autowired
        ShopRepository shopRepository;
        @Autowired
        ItemRepository itemRepository;
        @Autowired
        ItemService itemService;
        @Autowired
        OwnerRepository ownerRepository;

        @Test
        @Transactional(propagation = Propagation.NOT_SUPPORTED)
        void 재고_동시성_테스트() throws InterruptedException {
            Owner owner = createOwner("test-owner-concurrent-2", "test-password");
            Shop shop = createShop("test-concurrent-2", owner);

            Category category = Category.ELECTRONIC;
            int price = 100000;
            int stock = 1000;
            String itemName = "test-item";

            ItemSave itemSave = new ItemSave(itemName, stock, price, category, shop.getId());
            Long itemId = itemService.addItem(owner, itemSave);

            int threadCount = 100;
            ExecutorService executorService = Executors.newFixedThreadPool(16); // ThreadPool 구성
            CountDownLatch latch = new CountDownLatch(threadCount); // 다른 스레드에서 작업이 완료될 때까지 대기

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

            assertThat(item.getStock()).isEqualTo(stock - threadCount);
        }
        //region PRIVATE METHOD
        private Shop createShop(String shopName, Owner owner) {
            Shop shop = Shop.createShop(new ShopSave(shopName), owner);
            return shopRepository.save(shop);
        }

        private Owner createOwner(String loginId, String password) {
            OwnerSign ownerSignA = new OwnerSign(loginId, password);
            Owner owner = Owner.createOwner(ownerSignA);
            return ownerRepository.save(owner);
        }
        //endregion
    }
    //endregion

    //region PRIVATE METHOD
    private Shop createShop(String shopName, Owner owner) {
        Shop shop = Shop.createShop(new ShopSave(shopName), owner);
        return shopRepository.save(shop);
    }

    private Owner createOwner(String loginId, String password) {
        OwnerSign ownerSignA = new OwnerSign(loginId, password);
        Owner owner = Owner.createOwner(ownerSignA);
        return ownerRepository.save(owner);
    }
    //endregion
}