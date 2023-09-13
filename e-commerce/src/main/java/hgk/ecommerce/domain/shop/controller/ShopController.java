package hgk.ecommerce.domain.shop.controller;

import hgk.ecommerce.domain.common.annotation.AuthCheck;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.shop.dto.request.ShopSaveDto;
import hgk.ecommerce.domain.shop.dto.response.ShopInfo;
import hgk.ecommerce.domain.shop.service.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shops")
public class ShopController {
    private final ShopService shopService;

    @GetMapping
    public List<ShopInfo> getShopList(@AuthCheck Owner owner,
                                      @RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "5") Integer count) {
        return shopService.getShops(owner, page, count);
    }

    @PostMapping
    public void enrollShop(@AuthCheck Owner owner,
                           @Valid @RequestBody ShopSaveDto shopSaveDto) {
        shopService.enrollShop(owner, shopSaveDto);
    }
}
