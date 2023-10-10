package hgk.ecommerce.domain.item.controller;

import hgk.ecommerce.domain.common.annotation.AuthCheck;
import hgk.ecommerce.domain.item.dto.request.ItemEditDto;
import hgk.ecommerce.domain.item.dto.request.ItemFileDto;
import hgk.ecommerce.domain.item.dto.request.ItemSaveDto;
import hgk.ecommerce.domain.item.dto.request.ItemSearch;
import hgk.ecommerce.domain.item.dto.response.ItemInfo;
import hgk.ecommerce.domain.item.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static hgk.ecommerce.global.swagger.SwaggerConst.*;
import static org.springframework.http.MediaType.*;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;


    @PostMapping("/search")
    @Operation(summary = "아이템 검색", tags = USER)
    public List<ItemInfo> getItems(
            @RequestBody @Valid ItemSearch itemSearch,
            @RequestParam(defaultValue = "1", required = false) Integer page,
            @RequestParam(defaultValue = "5", required = false) Integer size) {
        return itemService.searchItems(itemSearch, page, size);
    }

    @GetMapping("/{shopId}/list")
    @Operation(summary = "가게내 아이템 가져오기", tags = OWNER)
    public List<ItemInfo> getOwnerShops(@AuthCheck(role = AuthCheck.Role.OWNER) Long ownerId,
                                        @PathVariable Long shopId,
                                        @RequestParam(defaultValue = "1", required = false) Integer page,
                                        @RequestParam(defaultValue = "5", required = false) Integer size) {
        return itemService.getItemsByShop(ownerId, shopId, page, size);
    }

    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "아이템 등록하기", tags = OWNER)
    public void registerItem(@AuthCheck(role = AuthCheck.Role.OWNER) Long ownerId, @Valid ItemSaveDto itemSave) {
        itemService.enrollItem(ownerId, itemSave);
    }

    @PatchMapping(consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "아이템 수정", tags = OWNER)
    public void editItem(@AuthCheck(role = AuthCheck.Role.OWNER) Long ownerId,
                         @Valid ItemEditDto itemEdit) {
        itemService.editItem(ownerId, itemEdit);
    }

    @PutMapping(consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "아이템 이미지 바꾸기", tags = OWNER)
    public void changeItemImage(@AuthCheck(role = AuthCheck.Role.OWNER) Long ownerId,
                                @Valid ItemFileDto itemFileDto) {
        itemService.changeItemImage(ownerId, itemFileDto);
    }
}
