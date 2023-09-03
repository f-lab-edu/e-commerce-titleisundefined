package hgk.ecommerce.domain.payment.controller;

import hgk.ecommerce.domain.common.annotation.AuthCheck;
import hgk.ecommerce.domain.payment.dto.PaymentCharge;
import hgk.ecommerce.domain.payment.service.PaymentService;
import hgk.ecommerce.domain.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public void chargePoint(@Valid @RequestBody PaymentCharge paymentCharge,
                            @AuthCheck User user) {
        paymentService.chargePoint(user, paymentCharge.getPoint());
    }
}
