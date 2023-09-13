package hgk.ecommerce.domain.cart.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CartItemSaveDto {
    @NotNull(message = "아이템을 선택해주세요")
    private Long itemId;
    @NotNull(message = "수량을 선택해주세요")
    private Integer count;
}
