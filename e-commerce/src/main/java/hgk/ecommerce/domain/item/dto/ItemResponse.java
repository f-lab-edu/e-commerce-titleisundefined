package hgk.ecommerce.domain.item.dto;

import hgk.ecommerce.domain.item.Item;
import lombok.Getter;

@Getter
public class ItemResponse {
    private Long item_id;
    private String name;
    private Integer price;
    private Category category;

    public ItemResponse(Item item) {
       this.item_id = item.getId();
       this.name = item.getName();
       this.price = item.getPrice();
       this.category = item.getCategory();
    }
}
