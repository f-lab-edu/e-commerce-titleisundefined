package hgk.ecommerce.domain.shop.service;

import hgk.ecommerce.domain.common.exceptions.DuplicatedException;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.owner.dto.request.OwnerSignUpDto;
import hgk.ecommerce.domain.owner.repository.OwnerRepository;
import hgk.ecommerce.domain.shop.dto.request.ShopSaveDto;
import hgk.ecommerce.domain.shop.dto.response.ShopInfo;
import hgk.ecommerce.domain.shop.repository.ShopRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ShopServiceTest {

    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    ShopService shopService;
    @Autowired
    ShopRepository shopRepository;
    @PersistenceContext
    EntityManager em;

    Owner ownerA;
    Owner ownerB;

    @BeforeEach
    void beforeEach() {
        ownerA = createOwner("test-owner-a", "test-password");
        ownerB = createOwner("test-owner-b", "test-password");
    }

    //region 가게 등록 테스트

    @Test
    void 정상_등록() {
        ShopSaveDto shopSaveDto = createShopSaveForm("owner-a");
        shopService.enrollShop(ownerA.getId(), shopSaveDto);

        flushAndClearPersistence();

        List<ShopInfo> shops = shopService.getShops(ownerA.getId(), 1, 5);
        assertThat(shops.size()).isEqualTo(1);
        ShopInfo shopInfo = shops.get(0);

        assertThat(shopInfo.getShopId()).isNotNull();
        assertThat(shopInfo.getShopName()).isEqualTo(shopSaveDto.getShopName());
        assertThat(shopInfo.getCreateDate()).isNotNull();
    }

    @Test
    void 중복_이름_등록() {
        ShopSaveDto shopSaveDto = createShopSaveForm("owner-a");
        shopService.enrollShop(ownerA.getId(), shopSaveDto);

        flushAndClearPersistence();

        assertThatThrownBy(() -> shopService.enrollShop(ownerA.getId(), shopSaveDto))
                .isInstanceOf(DuplicatedException.class);
    }

    //endregion

    //region 가게 조회 테스트

    @Test
    void 정상_조회() {
        List<ShopSaveDto> shopSaveDtos = new ArrayList<>();

        int loopCount = 100;

        for (int i = 0; i < loopCount; i++) {
            ShopSaveDto shopSaveForm = createShopSaveForm("test-name-" + i);
            shopService.enrollShop(ownerA.getId(), shopSaveForm);
            shopSaveDtos.add(shopSaveForm);
        }

        flushAndClearPersistence();

        assertThat(shopService.getShops(ownerA.getId(), 1, loopCount).size()).isEqualTo(loopCount);
        assertThat(shopService.getShops(ownerA.getId(), 1, loopCount + 1).size()).isEqualTo(loopCount);
        assertThat(shopService.getShops(ownerA.getId(), 1, loopCount - 1).size()).isEqualTo(loopCount - 1);

    }

    //endregion

    //region Stub Method

    private Owner createOwner(String loginId, String password) {
        Owner owner = Owner.createOwner(new OwnerSignUpDto(loginId, password));
        return ownerRepository.save(owner);
    }

    private void flushAndClearPersistence() {
        em.flush();
        em.clear();
    }

    private ShopSaveDto createShopSaveForm(String shopName) {
        return new ShopSaveDto(shopName);
    }

    //endregion
}