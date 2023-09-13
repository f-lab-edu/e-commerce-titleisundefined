package hgk.ecommerce.domain.shop.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import hgk.ecommerce.domain.shop.Shop;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ShopInfo {
    private Long shopId;
    private String shopName;
    private LocalDateTime createDate;
    public ShopInfo(Shop shop) {
        shopId = shop.getId();
        shopName = shop.getName();
        createDate = shop.getCreateDate();
    }
}
