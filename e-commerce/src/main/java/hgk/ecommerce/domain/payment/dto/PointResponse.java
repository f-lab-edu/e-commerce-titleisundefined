package hgk.ecommerce.domain.payment.dto;

import hgk.ecommerce.domain.payment.Payment;
import lombok.Getter;

@Getter
public class PointResponse {
    private Integer point;

    public PointResponse(Payment payment) {
        this.point = payment.getPoint();
    }
}
