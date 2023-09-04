package hgk.ecommerce.domain.review.dto;

import hgk.ecommerce.domain.review.Review;
import hgk.ecommerce.domain.user.User;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class ReviewResponse {
    private Long reviewId;
    private String content;
    private BigDecimal score;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    public ReviewResponse(Review review) {
        this.reviewId = review.getId();
        this.content = review.getContent();
        this.score = review.getScore();
        this.createDate = review.getCreateDate();
        this.modifyDate = review.getModifyDate();
    }
}
