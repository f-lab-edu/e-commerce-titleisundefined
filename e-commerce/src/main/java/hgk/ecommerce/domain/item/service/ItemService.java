package hgk.ecommerce.domain.item.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hgk.ecommerce.domain.common.exception.InvalidRequest;
import hgk.ecommerce.domain.common.exception.NoResourceException;
import hgk.ecommerce.domain.item.Item;
import hgk.ecommerce.domain.item.ItemImage;
import hgk.ecommerce.domain.item.QItem;
import hgk.ecommerce.domain.item.dto.*;
import hgk.ecommerce.domain.item.repository.ItemRepository;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.shop.Shop;
import hgk.ecommerce.domain.shop.dto.response.ShopResponse;
import hgk.ecommerce.domain.shop.service.ShopService;
import hgk.ecommerce.domain.storage.dto.ImageSave;
import hgk.ecommerce.domain.storage.repository.ItemImageRepository;
import hgk.ecommerce.global.storage.NaverObjectStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemImageRepository itemImageRepository;
    private final ShopService shopService;
    private final JPAQueryFactory queryFactory;
    private final NaverObjectStorage storageService;

    @Transactional(readOnly = true)
    public ItemResponseDetail getItem(Long itemId) {
        Item item = getItemEntity(itemId);
        ItemImage itemImage = itemImageRepository.findItemImageByItem(item);
        return new ItemResponseDetail(item, itemImage);
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> getItems(ItemSearchCond searchCond, Integer page, Integer count) {
        if (searchCond.getTitle() == null && searchCond.getCategory() == null) {
            throw new InvalidRequest("검색어나 카테고리를 선택해주세요.", HttpStatus.BAD_REQUEST);
        }

        List<Item> items = getItemsBySearchCond(searchCond, page, count);

        return items.stream()
                .map(ItemResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDetail> getShopItems(Owner owner, Long shopId, Integer page, Integer size) {
        Shop shop = shopService.getShopEntity(owner, shopId);

        PageRequest of = PageRequest.of(page - 1, size);
        Page<Item> items = itemRepository.findItemsByShopId(shop.getId(), of);

        return items.stream()
                .map(itemDetailMapping())
                .toList();
    }

    @Transactional
    public Long addItem(Owner owner, ItemSave itemSave) {
        Shop shop = shopService.getShopEntity(owner, itemSave.getShopId());
        ImageSave imageSave = new ImageSave(itemSave.getFile());

        Item item = itemRepository.save(Item.createItem(itemSave, shop));
        storageService.uploadItemImage(imageSave, item);

        return item.getId();
    }

    @Transactional
    public void editItem(Owner owner, Long itemId, ItemEdit itemEdit) {
        Item item = getItemEntity(itemId);

        shopService.checkShopAuth(owner, item.getShop());

        item.editItem(itemEdit);
    }

    @Transactional(readOnly = true)
    public Item getItemEntity(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NoResourceException("아이템을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
        });
    }

    @Transactional
    public void decreaseStock(Long itemId, Integer quantity) {
        Item item = itemRepository.findItemWithLock(itemId).orElseThrow(NoSuchElementException::new);

        checkItemStatus(item);
        item.decreaseStock(quantity);

        itemRepository.saveAndFlush(item);
    }

    @Transactional
    public void increaseStock(Long itemId, Integer quantity) {
        Item item = itemRepository.findItemWithLock(itemId).orElseThrow(NoSuchElementException::new);
        checkItemStatus(item);
        item.increaseStock(quantity);
        itemRepository.saveAndFlush(item);
    }


    //region PRIVATE METHOD
    private void checkItemStatus(Item item) {
        if (!item.getStatus().equals(ItemStatus.ACTIVE)) {
            throw new InvalidRequest("판매가 중지된 아이템 입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private List<Item> getItemsBySearchCond(ItemSearchCond searchCond, Integer page, Integer count) {
        return queryFactory
                .selectFrom(QItem.item)
                .where(categoryCond(searchCond.getCategory()),
                        titleCond(searchCond.getTitle()))
                .offset(page - 1)
                .limit(count)
                .fetch();
    }

    private BooleanExpression categoryCond(String categoryName) {
        return categoryName != null ? QItem.item.category.eq(Category.valueOf(categoryName)) : null;
    }

    private BooleanExpression titleCond(String title) {
        return title != null ? QItem.item.name.like(title + "%") : null;
    }

    private Function<Item, ItemResponseDetail> itemDetailMapping() {
        return (item) -> new ItemResponseDetail(item, itemImageRepository.findItemImageByItem(item));
    }

    //endregion
}
