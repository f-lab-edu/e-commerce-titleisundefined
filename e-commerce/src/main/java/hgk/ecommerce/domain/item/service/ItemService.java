package hgk.ecommerce.domain.item.service;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hgk.ecommerce.domain.common.exceptions.AuthenticationException;
import hgk.ecommerce.domain.common.exceptions.NoResourceException;
import hgk.ecommerce.domain.item.Item;
import hgk.ecommerce.domain.item.QItem;
import hgk.ecommerce.domain.item.dto.request.ItemEditDto;
import hgk.ecommerce.domain.item.dto.request.ItemSaveDto;
import hgk.ecommerce.domain.item.dto.request.ItemSearch;
import hgk.ecommerce.domain.item.dto.response.ItemInfo;
import hgk.ecommerce.domain.item.enums.Category;
import hgk.ecommerce.domain.item.repository.ItemRepository;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.shop.Shop;
import hgk.ecommerce.domain.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ShopService shopService;
    private final JPAQueryFactory jpaQueryFactory;

    @Transactional(readOnly = true)
    public List<ItemInfo> getItemsByShop(Owner owner, Long shopId, Integer page, Integer count) {
        Shop shop = shopService.getShopEntity(shopId);
        checkAuth(shop.getOwner().getId(), owner.getId());

        PageRequest paging = PageRequest.of(page - 1, count);

        Page<Item> items = itemRepository.findItemsByShopId(shop.getId(), paging);

        return items.stream()
                .map(ItemInfo::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ItemInfo> searchItems(ItemSearch itemSearch, Integer page, Integer count) {
        List<Item> items = findItems(itemSearch, page, count);

        return items.stream().map(ItemInfo::new).toList();
    }

    @Transactional(readOnly = true)
    public Item getItemEntity(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NoResourceException("아이템이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        });
    }

    @Transactional
    public void enrollItem(Owner owner, ItemSaveDto itemSaveDto) {
        Shop shop = shopService.getShopEntity(itemSaveDto.getShopId());
        checkAuth(shop.getOwner().getId(), owner.getId());

        Item item = Item.createItem(itemSaveDto, shop);

        itemRepository.save(item);
    }

    @Transactional
    public void editItem(Owner owner, ItemEditDto itemEditDto) {
        Item item = getItemFetchShop(itemEditDto);
        checkAuth(item.getShop().getOwner().getId(), owner.getId());

        item.editItem(itemEditDto);
    }

    @Transactional
    public void increaseStock(Long itemId, Integer quantity) {
        Item item = itemRepository.findItemWithLock(itemId).orElseThrow(() -> {
            throw new NoResourceException("아이템을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
        });

        item.increaseStock(quantity);
    }

    @Transactional
    public void decreaseStock(Long itemId, Integer quantity) {
        Item item = itemRepository.findItemWithLock(itemId).orElseThrow(() -> {
            throw new NoResourceException("아이템을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
        });

        item.decreaseStock(quantity);
    }

    //region PRIVATE METHOD
    private Item getItemFetchShop(ItemEditDto itemEditDto) {
        return itemRepository.findItemFetchShop(itemEditDto.getItemId()).orElseThrow(() -> {
            throw new NoResourceException("상품이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        });
    }

    private void checkAuth(Long realOwner, Long currentOwner) {
        if(!realOwner.equals(currentOwner)) {
            throw new AuthenticationException("접근 권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private List<Item> findItems(ItemSearch itemSearch, Integer page, Integer count) {
        return jpaQueryFactory
                .selectFrom(QItem.item)
                .where(categoryCond(itemSearch.getCategory()),
                        titleCond(itemSearch.getName()))
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
