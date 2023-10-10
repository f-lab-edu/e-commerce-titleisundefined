package hgk.ecommerce.domain.shop.service;

import hgk.ecommerce.domain.common.exceptions.AuthorizationException;
import hgk.ecommerce.domain.common.exceptions.DuplicatedException;
import hgk.ecommerce.domain.common.exceptions.NoResourceException;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.owner.service.OwnerService;
import hgk.ecommerce.domain.shop.Shop;
import hgk.ecommerce.domain.shop.dto.request.ShopSaveDto;
import hgk.ecommerce.domain.shop.dto.response.ShopInfo;
import hgk.ecommerce.domain.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.*;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository shopRepository;
    private final OwnerService ownerService;
    @Transactional(readOnly = true)
    public List<ShopInfo> getShops(Long ownerId, Integer page, Integer count) {
        Owner owner = ownerService.getCurrentOwnerById(ownerId);
        PageRequest paging = PageRequest.of(page - 1, count, ASC, "createDate");

        Page<Shop> shops = shopRepository.findShopsByOwnerId(owner.getId(), paging);

        return shops.stream().map(ShopInfo::new)
                .toList();
    }

    @Transactional
    public void enrollShop(Long ownerId, ShopSaveDto shopSaveDto) {
        Owner owner = ownerService.getCurrentOwnerById(ownerId);
        checkDuplicateName(shopSaveDto);

        Shop shop = Shop.createShop(shopSaveDto, owner);

        shopRepository.save(shop);
    }

    @Transactional(readOnly = true)
    public Shop getShopEntity(Long shopId) {
        return shopRepository.findById(shopId).orElseThrow(() -> {
            throw new NoResourceException("가게를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
        });
    }

    //region PRIVATE METHOD
    private void checkDuplicateName(ShopSaveDto shopSaveDto) {
        if (shopRepository.existsShopByName(shopSaveDto.getShopName())) {
            throw new DuplicatedException("중복된 가게이름이 있습니다.", HttpStatus.BAD_REQUEST);
        }
    }
    //endregion
}
