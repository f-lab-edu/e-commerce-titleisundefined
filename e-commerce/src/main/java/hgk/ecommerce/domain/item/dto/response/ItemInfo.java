package hgk.ecommerce.domain.item.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    private String imageLink;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal score;

    public ItemInfo(Item item, BigDecimal score) {
        name = item.getName();
        price = item.getPrice();
        category = item.getCategory();
        description = item.getDescription();
        itemId = item.getId();
        stock = item.getStock();
        this.score = score;
        this.imageLink = item.getImageFile().getVirtualName();
    }

    public ItemInfo(Item item) {
        name = item.getName();
        price = item.getPrice();
        category = item.getCategory();
        description = item.getDescription();
        itemId = item.getId();
        stock = item.getStock();
        imageLink = item.getImageFile().getVirtualName();
    }
}
