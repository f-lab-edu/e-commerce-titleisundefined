package hgk.ecommerce.domain.cart;


import hgk.ecommerce.domain.common.entity.BaseTimeEntity;
import hgk.ecommerce.domain.common.exceptions.InvalidRequest;
import hgk.ecommerce.domain.item.Item;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cart_item")
@EqualsAndHashCode(of = "id")
public class CartItem extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Column(nullable = false)
    private Integer quantity;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public static CartItem createCartItem(Cart cart, Item item, Integer quantity) {
        CartItem cartItem = new CartItem();

        cartItem.cart = cart;
        cartItem.quantity = quantity;
        cartItem.item = item;

        return cartItem;
    }

    public void increaseQuantity(int quantity) {
        this.quantity += quantity;
    }

    public void decreaseQuantity(int quantity) {
        if (this.quantity - quantity <= 0) {
            throw new InvalidRequest("1개 이상 담아야합니다.", HttpStatus.BAD_REQUEST);
        }
        this.quantity -= quantity;
    }
}
