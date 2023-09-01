package hgk.ecommerce.domain.shop.controller;

import hgk.ecommerce.domain.common.annotation.AuthCheck;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.shop.dto.request.ShopSave;
import hgk.ecommerce.domain.shop.dto.response.ShopResponse;
import hgk.ecommerce.domain.shop.service.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shop")
public class ShopController {
    private final ShopService shopService;

    @GetMapping("/{shopId}")
    public ShopResponse getShop(@AuthCheck Owner owner, @PathVariable Long shopId) {
        return shopService.getOwnShopById(owner, shopId);
    }

    @GetMapping("/list")
    public List<ShopResponse> getShopList(@AuthCheck Owner owner,
                                          @RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "5") Integer count) {
        return shopService.getOwnShops(owner, page, count);
    }

    @PostMapping
    public void addShop(@AuthCheck Owner owner,
                        @Valid @RequestBody ShopSave shopSave) {
        shopService.addShop(owner, shopSave);
    }
}
