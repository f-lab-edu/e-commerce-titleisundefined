package hgk.ecommerce.domain.shop.dto.response;

import hgk.ecommerce.domain.shop.Shop;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ShopResponse {
    private Long shopId;
    private String shopName;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    public ShopResponse(Shop shop) {
        this.shopId = shop.getId();
        this.shopName = shop.getName();
        this.createDate = shop.getCreateDate();
        this.modifyDate = shop.getModifyDate();
    }
}
