package hgk.ecommerce.domain.payment.controller;

import hgk.ecommerce.domain.common.annotation.AuthCheck;
import hgk.ecommerce.domain.payment.dto.PaymentCharge;
import hgk.ecommerce.domain.payment.dto.PointResponse;
import hgk.ecommerce.domain.payment.service.PaymentService;
import hgk.ecommerce.domain.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public void chargePoint(
            @AuthCheck User user,
            @Valid @RequestBody PaymentCharge paymentCharge
    ) {
        paymentService.chargePoint(user, paymentCharge.getPoint());
    }

    @GetMapping
    public PointResponse getPoint(@AuthCheck User user) {
        return paymentService.getPoint(user);
    }
}
