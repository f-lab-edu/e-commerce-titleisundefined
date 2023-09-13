package hgk.ecommerce.domain.review.repository;

import hgk.ecommerce.domain.review.Review;
import hgk.ecommerce.domain.review.dto.enums.ReviewStatus;
import hgk.ecommerce.domain.user.dto.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findReviewByIdAndStatusIs(Long reviewId, ReviewStatus status);
    Page<Review> findReviewsByItemIdAndStatusIs(Long itemId, ReviewStatus status, Pageable pageable);
    Page<Review> findReviewsByUserIdAndStatusIs(Long userId, ReviewStatus status, Pageable pageable);
    boolean existsReviewByOrderItemId(Long orderItemId);

    @Query("select nullif(AVG (r.score), 0.0)  from Review r where r.item.id = :itemId")
    BigDecimal getAverageScoreByItemId(@Param("itemId") Long itemId);
}
