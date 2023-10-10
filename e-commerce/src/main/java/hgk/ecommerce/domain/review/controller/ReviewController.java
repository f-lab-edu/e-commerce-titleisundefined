package hgk.ecommerce.domain.review.controller;

import hgk.ecommerce.domain.common.annotation.AuthCheck;
import hgk.ecommerce.domain.review.dto.request.ReviewEditDto;
import hgk.ecommerce.domain.review.dto.request.ReviewSaveDto;
import hgk.ecommerce.domain.review.dto.response.ReviewInfo;
import hgk.ecommerce.domain.review.service.ReviewService;
import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.global.swagger.SwaggerConst;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static hgk.ecommerce.global.swagger.SwaggerConst.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{itemId}")
    @Operation(summary = "아이템 리뷰 조회", tags = USER)
    public List<ReviewInfo> getItemReviews(@PathVariable Long itemId,
                                           @RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "5") Integer count) {
        return reviewService.getItemReviews(itemId, page, count);
    }

    @GetMapping
    @Operation(summary = "내 리뷰 조회", tags = USER)
    public List<ReviewInfo> getUserReviews(@AuthCheck(role = AuthCheck.Role.USER) Long userId,
                                           @RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "5") Integer count) {
        return reviewService.getUserReviews(userId, page, count);
    }

    @PostMapping("/{orderItemId}")
    @Operation(summary = "리뷰 등록", tags = USER)
    public void enrollReview(@AuthCheck(role = AuthCheck.Role.USER) Long userId,
                             @Valid @RequestBody ReviewSaveDto reviewSaveDto,
                             @PathVariable Long orderItemId) {
        reviewService.enrollReview(userId, reviewSaveDto, orderItemId);
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "리뷰 삭제", tags = USER)
    public void removeReview(@AuthCheck(role = AuthCheck.Role.USER) Long userId, @PathVariable Long reviewId) {
        reviewService.deleteReview(userId, reviewId);
    }

    @PatchMapping("/{reviewId}")
    @Operation(summary = "리뷰 수정", tags = USER)
    public void editReview(@AuthCheck(role = AuthCheck.Role.USER) Long userId,
                           @Valid @RequestBody ReviewEditDto reviewEditDto,
                           @PathVariable Long reviewId) {
        reviewService.editReview(userId, reviewId, reviewEditDto);
    }
}
