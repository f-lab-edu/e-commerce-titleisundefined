package hgk.ecommerce.domain.user.controller;

import hgk.ecommerce.domain.common.annotation.AuthCheck;
import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.domain.user.dto.request.UserLoginDto;
import hgk.ecommerce.domain.user.dto.request.UserSignUpDto;
import hgk.ecommerce.domain.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public void login(@Valid @RequestBody UserLoginDto userLogin) {
        userService.login(userLogin);
    }

    @PostMapping("/sign-up")
    public void signUp(@Valid @RequestBody UserSignUpDto userSign) {
        userService.signUp(userSign);
    }

    @DeleteMapping("/log-out")
    public void logOut(HttpSession session) {
        session.invalidate();
    }

    @DeleteMapping("/sign-out")
    public void signOut(@AuthCheck User user) {
        userService.signOut(user);
    }
}
