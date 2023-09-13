package hgk.ecommerce.domain.cart.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import hgk.ecommerce.domain.cart.CartItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CartItemInfo {
    private Long cartItemId;
    private Integer quantity;
    private String name;
    private Integer price;

    public CartItemInfo(CartItem cartItem) {
        cartItemId = cartItem.getId();
        quantity = cartItem.getQuantity();
        name = cartItem.getItem().getName();
        price = cartItem.getItem().getPrice();
    }
}
