package hgk.ecommerce.domain.shop.service;

import hgk.ecommerce.domain.common.exception.AuthenticationException;
import hgk.ecommerce.domain.common.exception.NoResourceException;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.owner.service.OwnerService;
import hgk.ecommerce.domain.shop.Shop;
import hgk.ecommerce.domain.shop.dto.request.ShopSave;
import hgk.ecommerce.domain.shop.dto.response.ShopResponse;
import hgk.ecommerce.domain.shop.repository.ShopRepository;
import hgk.ecommerce.domain.user.dto.exceptions.AlreadyExistException;
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
    public List<ShopResponse> getOwnShops(Owner owner, Integer page, Integer count) {

        PageRequest paging = PageRequest.of(page - 1, count);

        Page<Shop> shops = shopRepository.findShopsByOwnerId(owner.getId(), paging);

        return shops.getContent()
                .stream().map(ShopResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public ShopResponse getOwnShopById(Owner owner, Long shopId) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(() -> {
            throw new NoResourceException("존재하지 않는 가게 입니다.", HttpStatus.BAD_REQUEST);
        });

        if(!owner.equals(shop.getOwner()) ) {
            throw new AuthenticationException("접근 권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }

        return new ShopResponse(shop);
    }

    @Transactional
    public void addShop(Owner owner, ShopSave shopSave) {
        Shop shop = Shop.createShop(shopSave, owner);

        checkDuplicateName(shopSave.getShopName());

        shopRepository.save(shop);
    }

    //region PRIVATE METHOD
    private void checkDuplicateName(String shopName) {
        boolean isExist = shopRepository.existsShopByName(shopName);
        if(isExist) {
            throw new AlreadyExistException("이미 존재하는 가게 이름이 있습니다.", HttpStatus.BAD_REQUEST);
        }
    }
    //endregion

}
