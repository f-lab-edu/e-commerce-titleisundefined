package hgk.ecommerce.domain.payment.controller;

import hgk.ecommerce.domain.common.annotation.AuthCheck;
import hgk.ecommerce.domain.payment.service.PaymentService;
import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.global.swagger.SwaggerConst;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static hgk.ecommerce.global.swagger.SwaggerConst.*;

@RequestMapping("/payment")
@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/charge")
    @Operation(summary = "포인트 충전", tags = USER)
    public void chargePoint(@AuthCheck(role = AuthCheck.Role.USER) Long userId, @Valid @RequestBody ChargeRequestDto chargeRequestDto) {
        paymentService.increasePoint(userId, chargeRequestDto.getPoint());
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    static class ChargeRequestDto {
        @NotNull(message = "포인트를 입력해주세요.")
        private Integer point;
    }
}
