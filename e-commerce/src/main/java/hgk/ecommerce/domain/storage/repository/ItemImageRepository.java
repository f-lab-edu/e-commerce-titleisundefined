package hgk.ecommerce.domain.storage.repository;

import hgk.ecommerce.domain.item.Item;
import hgk.ecommerce.domain.item.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {
    ItemImage findItemImageByItem(Item item);
}
