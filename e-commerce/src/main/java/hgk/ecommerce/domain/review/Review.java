package hgk.ecommerce.domain.review;

import hgk.ecommerce.domain.common.entity.EntityBase;
import hgk.ecommerce.domain.item.Item;
import hgk.ecommerce.domain.order.OrderItem;
import hgk.ecommerce.domain.review.dto.ReviewEdit;
import hgk.ecommerce.domain.review.dto.ReviewSave;
import hgk.ecommerce.domain.review.dto.ReviewStatus;
import hgk.ecommerce.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "review")
public class Review extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(nullable = false, length = 200)
    private String content;

    @Column(nullable = false)
    private BigDecimal score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @Enumerated(EnumType.STRING)
    ReviewStatus status;

    public static Review createReview(User user, Item item, OrderItem orderItem, ReviewSave reviewSave) {
        Review review = new Review();
        review.content = reviewSave.getContent();
        review.score = reviewSave.getScore();
        review.user = user;
        review.item = item;
        review.orderItem = orderItem;
        review.status = ReviewStatus.ACTIVE;


        return review;
    }

    public void editReview(ReviewEdit reviewEdit) {
        this.content = reviewEdit.getContent();
        this.score = reviewEdit.getScore();
    }

    public void changeStatus(ReviewStatus status) {
        this.status = status;
    }
}
