package hgk.ecommerce.domain.owner.controller;

import hgk.ecommerce.domain.owner.dto.request.OwnerLoginDto;
import hgk.ecommerce.domain.owner.dto.request.OwnerSignUpDto;
import hgk.ecommerce.domain.owner.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/owners")
@RequiredArgsConstructor
@RestController
public class OwnerController {
    private final OwnerService ownerService;


    @PostMapping("/login")
    public void login(@Valid @RequestBody OwnerLoginDto ownerLogin) {
        ownerService.login(ownerLogin);
    }

    @PostMapping("/sign-up")
    public void signUp(@Valid @RequestBody OwnerSignUpDto ownerSign) {
        ownerService.signUp(ownerSign);
    }
}
