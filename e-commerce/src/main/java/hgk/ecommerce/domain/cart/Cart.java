package hgk.ecommerce.domain.cart;

import hgk.ecommerce.domain.common.entity.BaseTimeEntity;
import hgk.ecommerce.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cart")
@EqualsAndHashCode(of = "id")
public class Cart extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "cart")
    private List<CartItem> cartItems = new ArrayList<>();

    public static Cart createCart(User user) {
        Cart cart = new Cart();
        cart.user = user;
        return cart;
    }
}
