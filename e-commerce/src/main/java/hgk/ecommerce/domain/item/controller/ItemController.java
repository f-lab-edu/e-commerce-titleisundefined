package hgk.ecommerce.domain.item.controller;

import hgk.ecommerce.domain.common.annotation.AuthCheck;
import hgk.ecommerce.domain.item.dto.request.ItemEditDto;
import hgk.ecommerce.domain.item.dto.request.ItemSaveDto;
import hgk.ecommerce.domain.item.dto.request.ItemSearch;
import hgk.ecommerce.domain.item.dto.response.ItemInfo;
import hgk.ecommerce.domain.item.service.ItemService;
import hgk.ecommerce.domain.owner.Owner;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;


    @GetMapping(value = "/search")
    public List<ItemInfo> getItems(
            @RequestBody @Valid ItemSearch itemSearch,
            @RequestParam(defaultValue = "1", required = false) Integer page,
            @RequestParam(defaultValue = "5", required = false) Integer size) {
        return itemService.searchItems(itemSearch, page, size);
    }

    @GetMapping("/{shopId}/list")
    public List<ItemInfo> getOwnerShops(@AuthCheck Owner owner,
                                        @PathVariable Long shopId,
                                        @RequestParam(defaultValue = "1", required = false) Integer page,
                                        @RequestParam(defaultValue = "5", required = false) Integer size) {
        return itemService.getItemsByShop(owner, shopId, page, size);
    }

    @PostMapping
    public void registerItem(@AuthCheck Owner owner, @Valid @RequestBody ItemSaveDto itemSave) {
        itemService.enrollItem(owner, itemSave);
    }

    @PatchMapping
    public void editItem(@AuthCheck Owner owner,
                         @Valid @RequestBody ItemEditDto itemEdit) {
        itemService.editItem(owner, itemEdit);
    }
}
