package hgk.ecommerce.domain.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemSave {
    @NotBlank(message = "이름은 공백일 수 없습니다.")
    private String name;
    @NotNull(message = "재고는 공백일 수 없습니다.")
    private Integer stock;
    @NotNull(message = "가격은 공백일 수 없습니다.")
    private Integer price;
    @NotNull(message = "카테고리는 공백일 수 없습니다.")
    private Category category;
    @NotNull(message = "상품은 공백일 수 없습니다.")
    private Long shopId;
}
