package hgk.ecommerce.domain.item.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import hgk.ecommerce.domain.item.Item;
import hgk.ecommerce.domain.item.dto.enums.Category;
import hgk.ecommerce.domain.item.dto.enums.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ItemInfo {
    private String name;
    private Integer price;
    private Category category;
    private String description;
    private Long itemId;
    private Integer stock;
    private ItemStatus status;
    private BigDecimal score;

    public ItemInfo(Item item, BigDecimal score) {
        name = item.getName();
        price = item.getPrice();
        category = item.getCategory();
        description = item.getDescription();
        itemId = item.getId();
        stock = item.getStock();
        status = item.getStatus();
        this.score = score;
    }
}
