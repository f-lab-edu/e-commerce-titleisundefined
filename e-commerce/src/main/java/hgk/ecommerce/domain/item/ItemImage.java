package hgk.ecommerce.domain.item;

import hgk.ecommerce.domain.common.entity.ImageEntityBase;
import hgk.ecommerce.domain.storage.dto.ImageSave;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "item_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class ItemImage extends ImageEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_image_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public static ItemImage createItemImage(ImageSave imageSave, Item item) {
        ItemImage itemFile = new ItemImage();
        itemFile.originalName = imageSave.getOriginalName();
        itemFile.virtualName = imageSave.getVirtualName();
        itemFile.item = item;

        return itemFile;
    }
}
