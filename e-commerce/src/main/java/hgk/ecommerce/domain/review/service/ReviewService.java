package hgk.ecommerce.domain.review.service;

import hgk.ecommerce.domain.common.exception.AuthenticationException;
import hgk.ecommerce.domain.common.exception.NoResourceException;
import hgk.ecommerce.domain.order.OrderItem;
import hgk.ecommerce.domain.order.service.OrderService;
import hgk.ecommerce.domain.review.Review;
import hgk.ecommerce.domain.review.dto.ReviewEdit;
import hgk.ecommerce.domain.review.dto.ReviewResponse;
import hgk.ecommerce.domain.review.dto.ReviewSave;
import hgk.ecommerce.domain.review.dto.ReviewStatus;
import hgk.ecommerce.domain.review.repository.ReviewRepository;
import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.domain.user.dto.exceptions.AlreadyExistException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Predicate;

import static hgk.ecommerce.domain.review.dto.ReviewStatus.*;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrderService orderService;

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByItemId(Long itemId, Integer page, Integer count) {
        PageRequest paging = PageRequest.of(page - 1, count);
        List<Review> reviews = reviewRepository.findReviewsByItemId(itemId, paging);

        reviews = reviews.stream()
                .filter(statusEq(ACTIVE))
                .toList();

        return reviewsToResponseDto(reviews);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByUser(User user, Integer page, Integer count) {
        PageRequest paging = PageRequest.of(page - 1, count);
        List<Review> reviews = reviewRepository.findReviewsByUser(user.getId(), ACTIVE, paging);

        return reviewsToResponseDto(reviews);
    }

    @Transactional
    public Long enrollReview(User user, Long orderItemId, ReviewSave reviewSave) {
        OrderItem orderItem = orderService.findOrderItemFetchItem(user, orderItemId);

        if( existHistoryByOrderItem(orderItem) ) {
            throw new AlreadyExistException("이미 저장된 리뷰가 있습니다.", HttpStatus.BAD_REQUEST);
        }

        Review review = Review.createReview(user, orderItem.getItem(), orderItem, reviewSave);

        return reviewRepository.save(review).getId();
    }

    @Transactional
    public void deleteReview(User user, Long reviewId) {
        Review review = getReviewById(reviewId);
        checkReviewAuth(user, review);
        review.changeStatus(DELETE);
    }

    @Transactional
    public void editeReview(User user, Long reviewId, ReviewEdit reviewEdit) {
        Review review = getReviewById(reviewId);

        checkReviewAuth(user, review);

        review.editReview(reviewEdit);
    }


    //region PRIVATE METHOD

    private Predicate<Review> statusEq(ReviewStatus status) {
        return r -> r.getStatus().equals(status);
    }

    private boolean existHistoryByOrderItem(OrderItem orderItem) {
        return reviewRepository.existsReviewByOrderItem(orderItem);
    }

    private void checkReviewAuth(User user, Review review) {
        if (!review.getUser().getId().equals(user.getId())) {
            throw new AuthenticationException("접근 권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() -> {
            throw new NoResourceException("리뷰를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
        });
    }

    private List<ReviewResponse> reviewsToResponseDto(List<Review> reviews) {
        return reviews.stream().map(ReviewResponse::new).toList();
    }

    //endregion
}
