package hgk.ecommerce.domain.owner.controller;

import hgk.ecommerce.domain.common.dto.MyResponse;
import hgk.ecommerce.domain.owner.dto.OwnerLogin;
import hgk.ecommerce.domain.owner.dto.OwnerSign;
import hgk.ecommerce.domain.owner.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static hgk.ecommerce.domain.common.dto.MyResponse.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/owner")
public class OwnerController {
    private final OwnerService ownerService;

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody OwnerLogin ownerLogin) {
        ownerService.login(ownerLogin);
        return RESPONSE_OK;
    }

    @PostMapping("/sign-up")
    public ResponseEntity signUp(@Valid @RequestBody OwnerSign ownerSign) {
        ownerService.signUp(ownerSign);
        return RESPONSE_OK;
    }
}
