package hgk.ecommerce.domain.item.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Setter
public class ItemSave {
    @NotBlank(message = "이름은 공백일 수 없습니다.")
    private String name;
    @NotNull(message = "재고는 공백일 수 없습니다.")
    private Integer stock;
    @NotNull(message = "가격은 공백일 수 없습니다.")
    private Integer price;
    @NotNull(message = "카테고리는 공백일 수 없습니다.")
    private String category;
    @NotNull(message = "가게를 선택해주세요.")
    private Long shopId;
    @NotNull(message = "상품이 이미지를 삽입해주세요")
    private MultipartFile file;
}
