package hgk.ecommerce.domain.item.dto;

import hgk.ecommerce.domain.item.Item;
import hgk.ecommerce.domain.item.ItemImage;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class ItemResponseDetail extends ItemResponse{
    private String thumbNailPath;

    public ItemResponseDetail(Item item, ItemImage itemImage) {
        super(item);

        this.thumbNailPath = "https://kr.object.ncloudstorage.com/item-images/" + itemImage.getVirtualName();
    }
}
