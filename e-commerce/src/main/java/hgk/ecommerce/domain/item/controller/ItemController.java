package hgk.ecommerce.domain.item.controller;

import hgk.ecommerce.domain.common.annotation.AuthCheck;
import hgk.ecommerce.domain.common.exception.InvalidRequest;
import hgk.ecommerce.domain.item.dto.ItemEdit;
import hgk.ecommerce.domain.item.dto.ItemResponse;
import hgk.ecommerce.domain.item.dto.ItemSave;
import hgk.ecommerce.domain.item.dto.ItemSearchCond;
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

    @GetMapping(value = "/search", produces = "application/json; charset=utf8")
    public List<ItemResponse> getItems(
            @Valid ItemSearchCond searchCond,
            @RequestParam(defaultValue = "1", required = false) Integer page,
            @RequestParam(defaultValue = "5", required = false) Integer count
    ) {
        return itemService.getItems(searchCond, page , count);
    }

    @GetMapping("/{id}")
    public ItemResponse getItem(@PathVariable Long id) {
        return itemService.getItem(id);
    }

    @PostMapping
    public void registerItem(@AuthCheck Owner owner, @Valid @RequestBody ItemSave itemSave) {
        itemService.addItem(owner, itemSave);
    }

    @PostMapping("/{itemId}")
    public void editItem(@AuthCheck Owner owner,
                         @PathVariable Long itemId,
                         @Valid @RequestBody ItemEdit itemEdit) {
        itemService.editItem(owner, itemId, itemEdit);
    }
}
