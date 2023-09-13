package hgk.ecommerce.domain.item.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import hgk.ecommerce.domain.item.Item;
import hgk.ecommerce.domain.item.enums.Category;
import hgk.ecommerce.domain.item.enums.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public ItemInfo(Item item) {
        name = item.getName();
        price = item.getPrice();
        category = item.getCategory();
        description = item.getDescription();
        itemId = item.getId();
        stock = item.getStock();
        status = item.getStatus();
    }
}
