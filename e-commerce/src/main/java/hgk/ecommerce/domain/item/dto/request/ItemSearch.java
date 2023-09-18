package hgk.ecommerce.domain.item.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import hgk.ecommerce.domain.item.dto.enums.Category;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.*;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class ItemSearch {
    @Nullable
    private String name;

    @Schema(name = "category", allowableValues = {"BOOK", "CLOTHES", "ALBUM", "ELECTRONICS", "FOOD", "ETC"})
    @Nullable
    private Category category;

}
