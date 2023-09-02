package hgk.ecommerce.domain.cart.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import hgk.ecommerce.domain.cart.CartItem;
import hgk.ecommerce.domain.item.Item;
import lombok.Getter;

import static com.fasterxml.jackson.databind.PropertyNamingStrategies.*;

@Getter
@JsonNaming(SnakeCaseStrategy.class)
public class CartItemResponse {
    private Long cartItemId;
    private Integer price;
    private Integer quantity;

    public CartItemResponse(CartItem cartItem) {
        Item item = cartItem.getItem();
        this.cartItemId = cartItem.getId();
        this.price = item.getPrice();
        this.quantity = cartItem.getQuantity();
    }
}
