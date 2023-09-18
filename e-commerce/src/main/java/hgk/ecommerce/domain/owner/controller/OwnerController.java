package hgk.ecommerce.domain.owner.controller;

import hgk.ecommerce.domain.owner.dto.request.OwnerLoginDto;
import hgk.ecommerce.domain.owner.dto.request.OwnerSignUpDto;
import hgk.ecommerce.domain.owner.service.OwnerService;
import hgk.ecommerce.global.swagger.SwaggerConst;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static hgk.ecommerce.global.swagger.SwaggerConst.*;

@RequestMapping("/owners")
@RequiredArgsConstructor
@RestController
public class OwnerController {
    private final OwnerService ownerService;

    @PostMapping("/login")
    @Operation(summary = "로그인", tags = OWNER)
    public void login(@Valid @RequestBody OwnerLoginDto ownerLogin) {
        ownerService.login(ownerLogin);
    }

    @PostMapping("/sign-up")
    @Operation(summary = "회원가입", tags = OWNER)
    public void signUp(@Valid @RequestBody OwnerSignUpDto ownerSign) {
        ownerService.signUp(ownerSign);
    }
}
