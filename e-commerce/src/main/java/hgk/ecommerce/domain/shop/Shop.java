package hgk.ecommerce.domain.shop;

import hgk.ecommerce.domain.common.entity.EntityBase;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.shop.dto.request.ShopSave;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@Table(name = "shop")
public class Shop extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long id;

    @NotNull
    @Column(length = 100, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    public static Shop createShop(ShopSave shopSave, Owner owner) {
        Shop shop = new Shop();
        shop.name = shopSave.getShopName();
        shop.owner = owner;
        return shop;
    }
}
