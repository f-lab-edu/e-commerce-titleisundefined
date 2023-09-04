package hgk.ecommerce.domain.review.repository;

import hgk.ecommerce.domain.order.OrderItem;
import hgk.ecommerce.domain.review.Review;
import hgk.ecommerce.domain.review.dto.ReviewStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

//    @Query("select r from Review r join fetch r.user where r.item.id = :itemId")
//    List<Review> getReviewsFetchUserByItemId(@Param("itemId")Long itemId, Pageable pageable);

    List<Review> findReviewsByItemId(Long itemId, Pageable pageable);

    @Query("select r from Review r where r.user.id = :userId and r.status = :status")
    List<Review> findReviewsByUser(@Param("userId") Long userId, @Param("status") ReviewStatus status, Pageable pageable);

    boolean existsReviewByOrderItem(OrderItem orderItem);
}
