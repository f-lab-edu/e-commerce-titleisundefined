package hgk.ecommerce.domain.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemEdit {
    @NotBlank(message = "이름은 공백일 수 없습니다.")
    private String name;
    @NotNull(message = "재고는 공백일 수 없습니다.")
    private Integer stock;
    @NotNull(message = "가격은 공백일 수 없습니다.")
    private Integer price;
    @NotNull(message = "아이템 상태를 선택해주세요.")
    private ItemStatus status;
    @NotNull(message = "아이템 카테고리를 선택해주세요.")
    private Category category;
}
