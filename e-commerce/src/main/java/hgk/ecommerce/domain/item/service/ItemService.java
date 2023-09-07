package hgk.ecommerce.domain.item.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hgk.ecommerce.domain.common.exception.InvalidRequest;
import hgk.ecommerce.domain.common.exception.NoResourceException;
import hgk.ecommerce.domain.item.Item;
import hgk.ecommerce.domain.item.QItem;
import hgk.ecommerce.domain.item.dto.*;
import hgk.ecommerce.domain.item.repository.ItemRepository;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.shop.Shop;
import hgk.ecommerce.domain.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ShopService shopService;
    private final JPAQueryFactory queryFactory;

    @Transactional(readOnly = true)
    public ItemResponse getItem(Long itemId) {
        Item item = getItemEntity(itemId);

        return new ItemResponse(item);
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

    @Transactional
    public Long addItem(Owner owner, ItemSave itemSave) {
        Shop shop = shopService.getShopEntity(owner, itemSave.getShopId());

        Item item = Item.createItem(itemSave, shop);

        return itemRepository.save(item).getId();
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
        if(!item.getStatus().equals(ItemStatus.ACTIVE)) {
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

    private BooleanExpression categoryCond(Category category) {
        return category != null ? QItem.item.category.eq(category) : null;
    }

    private BooleanExpression titleCond(String title) {
        return title != null ? QItem.item.name.like(title + "%") : null;
    }

    //endregion
}
