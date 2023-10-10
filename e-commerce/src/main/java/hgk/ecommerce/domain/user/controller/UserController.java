package hgk.ecommerce.domain.user.controller;

import hgk.ecommerce.domain.common.annotation.AuthCheck;
import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.domain.user.dto.request.UserLoginDto;
import hgk.ecommerce.domain.user.dto.request.UserSignUpDto;
import hgk.ecommerce.domain.user.service.UserService;
import hgk.ecommerce.global.swagger.SwaggerConst;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static hgk.ecommerce.domain.common.annotation.AuthCheck.*;
import static hgk.ecommerce.global.swagger.SwaggerConst.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping(path = "/login")
    @Operation(summary = "유저 로그인", tags = USER)
    public void login(@Valid @RequestBody UserLoginDto userLogin) {
        userService.login(userLogin);
    }

    @PostMapping(path = "/sign-up", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "유저 회원가입", tags = USER)
    public void signUp(@Valid UserSignUpDto userSign) {
        userService.signUp(userSign);
    }

    @DeleteMapping("/log-out")
    @Operation(summary = "유저 로그아웃", tags = USER)
    public void logOut(HttpSession session) {
        session.invalidate();
    }

    @DeleteMapping("/sign-out")
    @Operation(summary = "유저 회원탈퇴", tags = USER)
    public void signOut(@AuthCheck(role = Role.USER) Long userId) {
        userService.signOut(userId);
    }
}
