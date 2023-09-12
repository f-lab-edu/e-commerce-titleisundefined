package hgk.ecommerce.domain.item.controller;

import hgk.ecommerce.domain.common.annotation.AuthCheck;
import hgk.ecommerce.domain.item.dto.*;
import hgk.ecommerce.domain.item.service.ItemService;
import hgk.ecommerce.domain.owner.Owner;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping(value = "/search", produces = "application/json; charset=utf8")
    public List<ItemResponse> getItems(
            @RequestBody @Valid ItemSearchCond searchCond,
            @RequestParam(defaultValue = "1", required = false) Integer page,
            @RequestParam(defaultValue = "5", required = false) Integer size
    ) {
        return itemService.getItems(searchCond, page, size);
    }

    @GetMapping("/{itemId}")
    public ItemResponse getItem(@PathVariable Long itemId) {
        ItemResponseDetail item = itemService.getItem(itemId);
        return item;
    }

    @GetMapping("/{shopId}/list")
    public List<? extends ItemResponse> getShopItems(@AuthCheck Owner owner,
                                           @PathVariable Long shopId,
                                           @RequestParam(defaultValue = "1", required = false) Integer page,
                                           @RequestParam(defaultValue = "5", required = false) Integer size) {
        return itemService.getShopItems(owner, shopId, page, size);
    }

    @PostMapping
    public void registerItem(@AuthCheck Owner owner, @Valid ItemSave itemSave) {
        itemService.addItem(owner, itemSave);
    }

    @PostMapping("/{itemId}")
    public void editItem(@AuthCheck Owner owner,
                         @PathVariable Long itemId,
                         @Valid @RequestBody ItemEdit itemEdit) {
        itemService.editItem(owner, itemId, itemEdit);
    }
}
