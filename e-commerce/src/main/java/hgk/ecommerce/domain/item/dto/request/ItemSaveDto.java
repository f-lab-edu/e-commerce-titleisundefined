package hgk.ecommerce.domain.item.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import hgk.ecommerce.domain.item.dto.enums.Category;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor
@AllArgsConstructor
public class ItemSaveDto {
    @NotNull
    private Long shopId;
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
}
