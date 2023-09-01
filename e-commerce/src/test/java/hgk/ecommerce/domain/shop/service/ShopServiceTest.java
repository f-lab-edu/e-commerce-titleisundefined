package hgk.ecommerce.domain.shop.service;

import hgk.ecommerce.domain.common.exception.AuthenticationException;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.owner.dto.OwnerSign;
import hgk.ecommerce.domain.owner.repository.OwnerRepository;
import hgk.ecommerce.domain.owner.service.OwnerService;
import hgk.ecommerce.domain.shop.dto.request.ShopSave;
import hgk.ecommerce.domain.shop.dto.response.ShopResponse;
import hgk.ecommerce.domain.shop.repository.ShopRepository;
import hgk.ecommerce.domain.user.dto.exceptions.AlreadyExistException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ShopServiceTest {
    @Autowired
    ShopService shopService;
    @Autowired
    ShopRepository shopRepository;
    @Autowired
    OwnerService ownerService;
    @Autowired
    OwnerRepository ownerRepository;
    @PersistenceContext
    EntityManager em;

    Owner ownerA;
    Owner ownerB;

    @BeforeEach
    void beforeEach() {
        String ownerIdA = "test-owner-1";
        OwnerSign ownerSignA = new OwnerSign(ownerIdA, "test-password-1");
        ownerService.signUp(ownerSignA);
        String ownerIdB = "test-owner-2";
        OwnerSign ownerSignB = new OwnerSign(ownerIdB, "test-password-1");
        ownerService.signUp(ownerSignB);
        this.ownerA = ownerRepository.findOwnerByLoginId(ownerIdA).orElseThrow();
        this.ownerB = ownerRepository.findOwnerByLoginId(ownerIdB).orElseThrow();

        em.flush();
        em.clear();
    }

    //region 가게 등록
    @Test
    void 정상_등록() {
        ShopSave shopSaveA = new ShopSave("test-shop-a");
        shopService.addShop(ownerA, shopSaveA);
        ShopSave shopSaveB = new ShopSave("test-shop-b");
        shopService.addShop(ownerA, shopSaveB);
    }

    @Test
    void 중복_이름_등록() {
        ShopSave shopSaveA = new ShopSave("test-shop-a");
        shopService.addShop(ownerA, shopSaveA);
        ShopSave shopSaveB = new ShopSave("test-shop-a");
        assertThatThrownBy(() -> shopService.addShop(ownerA, shopSaveB))
                .isInstanceOf(AlreadyExistException.class);
    }
    //endregion

    //region 가게 조회
    @Test
    void 다중_조회() {
        int count = 10;
        for (int i = 0; i < count; i++) {
            shopService.addShop(ownerA, new ShopSave("shop-test-" + i));
        }
        em.flush();
        em.clear();

        assertThat(shopService.getOwnShops(ownerA, 1, count).size()).isEqualTo(count);
        assertThat(shopService.getOwnShops(ownerA, 1, 5).size()).isEqualTo(5);
    }

    @Test
    void 단일_조회() {
        int count = 5;
        for (int i = 0; i < count; i++) {
            shopService.addShop(ownerA, new ShopSave("shop-test-" + i));
        }
        em.flush();
        em.clear();

        List<ShopResponse> ownShops = shopService.getOwnShops(ownerA, 1, count);

        for (int i = 0; i < ownShops.size(); i++) {
            ShopResponse shopResponse = shopService.getOwnShopById(ownerA, ownShops.get(i).getShopId());
            assertThat(shopResponse.getShopName()).isEqualTo("shop-test-" + i);
        }

        //권한 없으면 실패
        assertThatThrownBy(() -> shopService.getOwnShopById(ownerB, ownShops.get(0).getShopId()))
                .isInstanceOf(AuthenticationException.class);
    }
    //endregion
}