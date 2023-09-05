package hgk.ecommerce.domain.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class CartItemSave {
    private Long itemId;
    private Integer quantity;
}
