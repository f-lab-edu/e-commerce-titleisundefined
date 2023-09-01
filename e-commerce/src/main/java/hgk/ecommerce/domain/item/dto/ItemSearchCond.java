package hgk.ecommerce.domain.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemSearchCond {
    private String title;
    private Category category;
}
