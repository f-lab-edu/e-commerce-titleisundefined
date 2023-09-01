package hgk.ecommerce.domain.item.dto;

import hgk.ecommerce.domain.item.Item;
import lombok.Getter;

@Getter
public class ItemResponse {
    private Long id;
    private String name;
    private Integer price;

    public ItemResponse(Item item) {
       this.id = item.getId();
       this.name = item.getName();
       this.price = item.getPrice();
    }
}
