package hgk.ecommerce.domain.review.service;

import hgk.ecommerce.domain.common.exceptions.AuthenticationException;
import hgk.ecommerce.domain.common.exceptions.InvalidRequest;
import hgk.ecommerce.domain.common.exceptions.NoResourceException;
import hgk.ecommerce.domain.order.Order;
import hgk.ecommerce.domain.order.OrderItem;
import hgk.ecommerce.domain.order.service.OrderService;
import hgk.ecommerce.domain.review.Review;
import hgk.ecommerce.domain.review.dto.request.ReviewEditDto;
import hgk.ecommerce.domain.review.dto.request.ReviewSaveDto;
import hgk.ecommerce.domain.review.dto.response.ReviewInfo;
import hgk.ecommerce.domain.review.repository.ReviewRepository;
import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.domain.user.dto.enums.Status;
import hgk.ecommerce.domain.user.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static hgk.ecommerce.domain.review.dto.enums.ReviewStatus.*;
import static org.springframework.data.domain.Sort.Direction.*;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrderService orderService;
    private final UserServiceImpl userService;

    @Transactional(readOnly = true)
    public List<ReviewInfo> getItemReviews(Long itemId, Integer page, Integer count) {
        PageRequest paging = PageRequest.of(page - 1, count, DESC, "createDate");
        Page<Review> reviews = reviewRepository.findReviewsByItemIdAndStatusIs(itemId, ACTIVE, paging);

        return reviews.stream()
                .map(ReviewInfo::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewInfo> getUserReviews(Long userId, Integer page, Integer count) {
        User user = userService.getCurrentUserById(userId);
        PageRequest paging = PageRequest.of(page - 1, count, DESC, "createDate");
        Page<Review> reviews = reviewRepository.findReviewsByUserIdAndStatusIs(user.getId(), ACTIVE, paging);

        return reviews.stream()
                .map(ReviewInfo::new)
                .toList();
    }

    @Transactional
    public Long enrollReview(Long userId, ReviewSaveDto reviewSaveDto, Long orderItemId) {
        User user = userService.getCurrentUserById(userId);
        OrderItem orderItem = orderService.getOrderItemById(orderItemId);
        Order order = orderItem.getOrder();

        checkOrderAuth(user, order);
        checkReviewHistory(orderItemId);

        Review review = Review.createReview(user, orderItem.getItem(), orderItem, reviewSaveDto);

        return reviewRepository.save(review).getId();
    }

    @Transactional
    public void editReview(Long userId, Long reviewId, ReviewEditDto reviewEditDto) {
        User user = userService.getCurrentUserById(userId);
        Review review = getReviewById(reviewId);
        checkReviewAuth(user, review);

        review.editReview(reviewEditDto);
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        User user = userService.getCurrentUserById(userId);
        Review review = getReviewById(reviewId);
        checkReviewAuth(user, review);

        review.deleteReview();
    }


    //region PRIVATE METHOD

    private Review getReviewById(Long reviewId) {
        return reviewRepository.findReviewByIdAndStatusIs(reviewId, ACTIVE).orElseThrow(() -> {
            throw new NoResourceException("리뷰를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
        });
    }

    private void checkReviewAuth(User user, Review review) {
        if(!user.getId().equals(review.getUser().getId())) {
            throw new AuthenticationException("접근 권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void checkOrderAuth(User user, Order order) {
        if(!order.getUser().getId().equals(user.getId())) {
            throw new AuthenticationException("접근 권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void checkReviewHistory(Long orderItemId) {
        if(reviewRepository.existsReviewByOrderItemId(orderItemId)) {
            throw new InvalidRequest("이미 등록된 리뷰가 있습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    //endregion

}
