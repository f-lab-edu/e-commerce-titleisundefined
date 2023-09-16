package hgk.ecommerce.domain.item.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import hgk.ecommerce.domain.item.dto.enums.Category;
import hgk.ecommerce.domain.item.dto.enums.ItemStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemEditDto {
    @NotNull
    private Long itemId;
    @NotEmpty(message = "상품 이름을 입력해주세요.")
    private String itemName;
    @NotNull(message = "재고를 입력해주세요.")
    private Integer stock;
    @NotNull(message = "가격을 입력해주세요.")
    private Integer price;
    @NotNull(message = "카테고리를 선택해주세요.")
    private Category category;
    @NotEmpty(message = "상품 설명을 입력해주세요.")
    private String description;
    @NotNull
    private ItemStatus itemStatus;
}
