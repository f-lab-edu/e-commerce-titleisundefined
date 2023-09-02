package hgk.ecommerce.domain.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CartItemSave {
    private Long itemId;
    private Integer quantity;
}
