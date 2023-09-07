package hgk.ecommerce.domain.user.controller;

import hgk.ecommerce.domain.common.annotation.AuthCheck;
import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.domain.user.dto.request.UserSign;
import hgk.ecommerce.domain.user.dto.request.UserLogin;
import hgk.ecommerce.domain.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static hgk.ecommerce.domain.common.dto.MyResponse.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody UserLogin userLogin) {
        userService.login(userLogin);
        return RESPONSE_OK;
    }

    @PostMapping("/sign-up")
    public ResponseEntity signUp(@Valid @RequestBody UserSign userSign) {
        userService.signUp(userSign);
        return RESPONSE_OK;
    }

    @DeleteMapping("/log-out")
    public ResponseEntity logOut(HttpSession session) {
        session.invalidate();
        return RESPONSE_OK;
    }

    @DeleteMapping("sign-out")
    public ResponseEntity signOut(@AuthCheck User user) {
        userService.delete(user);
        return RESPONSE_OK;
    }
}
