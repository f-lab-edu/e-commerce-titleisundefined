package hgk.ecommerce.domain.shop.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ShopSave {
    @NotBlank(message = "가게 이름은 공백일 수 없습니다.")
    private String shopName;
}
