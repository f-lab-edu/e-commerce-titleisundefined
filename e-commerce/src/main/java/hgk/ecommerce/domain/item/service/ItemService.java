package hgk.ecommerce.domain.item.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hgk.ecommerce.domain.common.annotation.RedisLock;
import hgk.ecommerce.domain.common.exceptions.AuthenticationException;
import hgk.ecommerce.domain.common.exceptions.NoResourceException;
import hgk.ecommerce.domain.item.Item;
import hgk.ecommerce.domain.item.QItem;
import hgk.ecommerce.domain.item.dto.request.ItemEditDto;
import hgk.ecommerce.domain.item.dto.request.ItemFileDto;
import hgk.ecommerce.domain.item.dto.request.ItemSaveDto;
import hgk.ecommerce.domain.item.dto.request.ItemSearch;
import hgk.ecommerce.domain.item.dto.response.ItemInfo;
import hgk.ecommerce.domain.item.dto.enums.Category;
import hgk.ecommerce.domain.item.repository.ItemRepository;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.review.repository.ReviewRepository;
import hgk.ecommerce.domain.review.service.ReviewService;
import hgk.ecommerce.domain.shop.Shop;
import hgk.ecommerce.domain.shop.service.ShopService;
import hgk.ecommerce.global.storage.ImageFile;
import hgk.ecommerce.global.storage.QImageFile;
import hgk.ecommerce.global.storage.service.ImageFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ShopService shopService;
    private final JPAQueryFactory jpaQueryFactory;
    private final ReviewRepository reviewRepository;
    private final ImageFileService imageFileService;

    @Transactional(readOnly = true)
    public List<ItemInfo> getItemsByShop(Owner owner, Long shopId, Integer page, Integer count) {
        Shop shop = shopService.getShopEntity(shopId);
        checkAuth(shop.getOwner().getId(), owner.getId());

        PageRequest paging = PageRequest.of(page - 1, count, ASC, "createDate");

        Page<Item> items = itemRepository.findItemsByShopId(shop.getId(), paging);

        return items.stream()
                .map(item -> new ItemInfo(item, getAverageScoreByItemId(item)))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ItemInfo> searchItems(ItemSearch itemSearch, Integer page, Integer count) {
        List<Item> items = findItems(itemSearch, page, count);

        return items.stream()
                .map(item -> new ItemInfo(item, getAverageScoreByItemId(item)))
                .toList();
    }

    @Transactional(readOnly = true)
    public Item getItemEntity(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NoResourceException("아이템이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        });
    }

    @Transactional
    public Long enrollItem(Owner owner, ItemSaveDto itemSaveDto) {
        Shop shop = shopService.getShopEntity(itemSaveDto.getShopId());
        checkAuth(shop.getOwner().getId(), owner.getId());


        ImageFile imageFile = imageFileService.saveImageFile(itemSaveDto.getFile());

        Item item = Item.createItem(itemSaveDto, shop, imageFile);

        return itemRepository.save(item).getId();
    }

    @Transactional
    public void editItem(Owner owner, ItemEditDto itemEditDto) {
        Item item = getItemFetchShop(itemEditDto.getItemId());
        checkAuth(item.getShop().getOwner().getId(), owner.getId());

        item.editItem(itemEditDto);
    }

    @RedisLock(key = "#itemId")
    public void increaseStock(Long itemId, Integer quantity) {
        Item item = getItemWithLock(itemId);

        item.increaseStock(quantity);
    }

    @RedisLock(key = "#itemId")
    public void decreaseStock(Long itemId, Integer quantity) {
        Item item = getItemWithLock(itemId);

        item.decreaseStock(quantity);
    }

    @Transactional
    public void changeItemImage(Owner owner, ItemFileDto itemFileDto) {
        Item item = getItemFetchShop(itemFileDto.getItemId());
        checkAuth(owner.getId(), item.getShop().getOwner().getId());

        imageFileService.putImageFile(item.getImageFile().getId(), itemFileDto.getFile());
    }


    //region PRIVATE METHOD

    private Item getItemWithLock(Long itemId) {
        return itemRepository.findItemWithLock(itemId).orElseThrow(() -> {
            throw new NoResourceException("아이템을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
        });
    }

    private Item getItemFetchShop(Long itemId) {
        return itemRepository.findItemFetchShop(itemId).orElseThrow(() -> {
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
                .leftJoin(QItem.item.imageFile, QImageFile.imageFile)
                .fetchJoin()
                .where(categoryCond(itemSearch.getCategory()),
                        titleCond(itemSearch.getName()))
                .offset(page - 1)
                .limit(count)
                .fetch();
    }

    private BooleanExpression categoryCond(Category category) {
        return category != null ? QItem.item.category.eq(category) : null;
    }

    private BigDecimal getAverageScoreByItemId(Item item) {
        return reviewRepository.getAverageScoreByItemId(item.getId());
    }

    private BooleanExpression titleCond(String title) {
        return title != null ? QItem.item.name.like(title + "%") : null;
    }

    //endregion
}
