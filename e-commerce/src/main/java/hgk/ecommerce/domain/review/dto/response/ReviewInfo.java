package hgk.ecommerce.domain.review.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import hgk.ecommerce.domain.review.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ReviewInfo {

    private Long reviewId;
    private String content;
    private BigDecimal score;

    public ReviewInfo(Review review) {
        reviewId = review.getId();
        content = review.getContent();
        score = review.getScore();
    }
}
