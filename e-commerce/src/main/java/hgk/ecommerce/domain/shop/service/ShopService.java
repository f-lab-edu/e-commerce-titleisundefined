package hgk.ecommerce.domain.shop.service;

import hgk.ecommerce.domain.common.exceptions.AuthorizationException;
import hgk.ecommerce.domain.common.exceptions.DuplicatedException;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.shop.Shop;
import hgk.ecommerce.domain.shop.dto.request.ShopSaveDto;
import hgk.ecommerce.domain.shop.dto.response.ShopInfo;
import hgk.ecommerce.domain.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository shopRepository;

    @Transactional(readOnly = true)
    public List<ShopInfo> getShops(Owner owner, Integer page, Integer count) {
        PageRequest paging = PageRequest.of(page - 1, count);

        Page<Shop> shops = shopRepository.findShopsByOwnerId(owner.getId(), paging);

        return shops.stream().map(ShopInfo::new)
                .toList();
    }

    @Transactional
    public void enrollShop(Owner owner, ShopSaveDto shopSaveDto) {
        checkDuplicateName(shopSaveDto);

        Shop shop = Shop.createShop(shopSaveDto, owner);

        shopRepository.save(shop);
    }

    //region PRIVATE METHOD
    private void checkDuplicateName(ShopSaveDto shopSaveDto) {
        if (shopRepository.existsShopByName(shopSaveDto.getShopName())) {
            throw new DuplicatedException("중복된 가게이름이 있습니다.", HttpStatus.BAD_REQUEST);
        }
    }
    //endregion
}
