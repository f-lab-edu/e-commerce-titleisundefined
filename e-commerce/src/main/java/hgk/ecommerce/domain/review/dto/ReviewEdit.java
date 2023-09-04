package hgk.ecommerce.domain.review.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ReviewEdit {
    @NotBlank
    private String content;
    @NotNull
    @Digits(integer = 1, fraction = 1)
    private BigDecimal score;
}
