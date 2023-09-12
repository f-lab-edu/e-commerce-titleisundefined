package hgk.ecommerce.domain.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemSearchCond {
    private String title;
    private String category;
}
