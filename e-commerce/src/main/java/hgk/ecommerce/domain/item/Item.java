package hgk.ecommerce.domain.item;

import hgk.ecommerce.domain.common.entity.EntityBase;
import hgk.ecommerce.domain.common.exception.NoResourceException;
import hgk.ecommerce.domain.item.dto.Category;
import hgk.ecommerce.domain.item.dto.ItemEdit;
import hgk.ecommerce.domain.item.dto.ItemSave;
import hgk.ecommerce.domain.item.dto.ItemStatus;
import hgk.ecommerce.domain.shop.Shop;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import static hgk.ecommerce.domain.item.dto.ItemStatus.*;

@Table(name = "items")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(of = "id")
public class Item extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @NotNull
    @Column(length = 50)
    private String name;

    @NotNull
    private Integer stock;

    @NotNull
    private Integer price;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    public static Item createItem(ItemSave itemSave, Shop shop) {
        Item item = new Item();
        item.name = itemSave.getName();
        item.stock = itemSave.getStock();
        item.price = itemSave.getPrice();
        item.status = ACTIVE;
        item.category = itemSave.getCategory();
        item.shop = shop;

        return item;
    }

    public void editItem(ItemEdit itemEdit) {
        this.name = itemEdit.getName();
        this.stock = itemEdit.getStock();
        this.price = itemEdit.getPrice();
        this.status = itemEdit.getStatus();
        this.category = itemEdit.getCategory();
    }

    public void increaseStock(int quantity) {
        this.stock += quantity;
    }

    public void decreaseStock(int quantity) {
        if (stock - quantity < 0) {
            String message = String.format("%s 상품은 %d개 까지만 구매 가능합니다.", this.name, this.stock);
            throw new NoResourceException(message, HttpStatus.BAD_REQUEST);
        }
        this.stock -= quantity;
    }
}
