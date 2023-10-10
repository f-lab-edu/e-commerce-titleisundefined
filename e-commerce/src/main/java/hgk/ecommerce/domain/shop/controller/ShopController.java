package hgk.ecommerce.domain.shop.controller;

import hgk.ecommerce.domain.common.annotation.AuthCheck;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.shop.dto.request.ShopSaveDto;
import hgk.ecommerce.domain.shop.dto.response.ShopInfo;
import hgk.ecommerce.domain.shop.service.ShopService;
import hgk.ecommerce.global.swagger.SwaggerConst;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static hgk.ecommerce.global.swagger.SwaggerConst.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shops")
public class ShopController {
    private final ShopService shopService;

    @GetMapping
    @Operation(summary = "본인 가게 검색", tags = OWNER)
    public List<ShopInfo> getShopList(@AuthCheck(role = AuthCheck.Role.OWNER) Long ownerId,
                                      @RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "5") Integer count) {
        return shopService.getShops(ownerId, page, count);
    }

    @PostMapping
    @Operation(summary = "가게 등록", tags = OWNER)
    public void enrollShop(@AuthCheck(role = AuthCheck.Role.OWNER) Long ownerId,
                           @Valid @RequestBody ShopSaveDto shopSaveDto) {
        shopService.enrollShop(ownerId, shopSaveDto);
    }
}
