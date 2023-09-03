package hgk.ecommerce.domain.payment.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentCharge {
    @Min(0)
    Integer point;
}
