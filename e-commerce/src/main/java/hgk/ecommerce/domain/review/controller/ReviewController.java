package hgk.ecommerce.domain.review.controller;

import hgk.ecommerce.domain.common.annotation.AuthCheck;
import hgk.ecommerce.domain.review.dto.ReviewEdit;
import hgk.ecommerce.domain.review.dto.ReviewResponse;
import hgk.ecommerce.domain.review.dto.ReviewSave;
import hgk.ecommerce.domain.review.service.ReviewService;
import hgk.ecommerce.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{itemId}")
    public List<ReviewResponse> getReviews(@PathVariable Long itemId,
                                           @RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "5") Integer count) {
        return reviewService.getReviewsByItemId(itemId, page, count);
    }

    @PutMapping("/{reviewId}")
    public void editReview(@AuthCheck User user,
                           @PathVariable Long reviewId,
                           @RequestBody ReviewEdit reviewEdit) {
        reviewService.editeReview(user, reviewId, reviewEdit);
    }

    @PostMapping("/{itemId}")
    public void enrollReview(@AuthCheck User user,
                             @PathVariable Long itemId,
                             @RequestBody ReviewSave reviewSave){
        reviewService.enrollReview(user, itemId, reviewSave);
    }

    @DeleteMapping("/{reviewId}")
    public void removeReview(@AuthCheck User user,
                             @PathVariable Long reviewId) {
        reviewService.deleteReview(user, reviewId);
    }
}
