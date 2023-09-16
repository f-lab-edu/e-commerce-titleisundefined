package hgk.ecommerce.domain.user.service;

import hgk.ecommerce.domain.common.exceptions.AuthenticationException;
import hgk.ecommerce.domain.common.exceptions.AuthorizationException;
import hgk.ecommerce.domain.common.exceptions.DuplicatedException;
import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.domain.user.dto.enums.Status;
import hgk.ecommerce.domain.user.dto.request.UserLoginDto;
import hgk.ecommerce.domain.user.dto.request.UserSignUpDto;
import hgk.ecommerce.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static hgk.ecommerce.domain.user.dto.enums.Status.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @PersistenceContext
    EntityManager em;

    //region 회원가입 테스트

    @Test
    void 정상_회원가입() {
        UserSignUpDto signUpDto = UserSignUpDto.builder()
                .loginId("test-login-id")
                .password("test-password")
                .nickname("test-nickname")
                .address("test-address")
                .build();

        userService.signUp(signUpDto);

        flushAndClearPersistence();

        User user = getUserByLoginId(signUpDto);

        assertThat(user.getId()).isNotNull();
        assertThat(user.getStatus()).isEqualTo(ACTIVE);
        assertThat(user.getAddress()).isEqualTo(signUpDto.getAddress());
        assertThat(user.getNickname()).isEqualTo(signUpDto.getNickname());
        assertThat(user.getLoginId()).isEqualTo(signUpDto.getLoginId());
        assertThat(user.getPassword()).isEqualTo(signUpDto.getPassword());
        assertThat(user.getCreateDate()).isNotNull();
        assertThat(user.getModifyDate()).isNotNull();

    }

    @Test
    void 중복_아이디_회원가입() {
        UserSignUpDto signUpDto1 = UserSignUpDto.builder()
                .loginId("test-login-id")
                .password("test-password")
                .nickname("test-nickname")
                .address("test-address")
                .build();
        UserSignUpDto signUpDto2 = UserSignUpDto.builder()
                .loginId("test-login-id")
                .password("new-password")
                .nickname("new-nickname")
                .address("new-address")
                .build();

        userService.signUp(signUpDto1);

        flushAndClearPersistence();

        assertThatThrownBy(() -> userService.signUp(signUpDto2))
                .isInstanceOf(DuplicatedException.class);
    }

    @Test
    void 중복_닉네임_회원가입() {
        UserSignUpDto signUpDto1 = UserSignUpDto.builder()
                .loginId("test-login-id")
                .password("test-password")
                .nickname("test-nickname")
                .address("test-address")
                .build();
        UserSignUpDto signUpDto2 = UserSignUpDto.builder()
                .loginId("new-login-id")
                .password("new-password")
                .nickname("test-nickname")
                .address("new-address")
                .build();

        userService.signUp(signUpDto1);

        flushAndClearPersistence();

        assertThatThrownBy(() -> userService.signUp(signUpDto2))
                .isInstanceOf(DuplicatedException.class);
    }

    //endregion

    //region 로그인 테스트

    @Test
    void 정상_로그인() {
        UserSignUpDto signUpDto = UserSignUpDto.builder()
                .loginId("test-login-id")
                .password("test-password")
                .nickname("test-nickname")
                .address("test-address")
                .build();

        userService.signUp(signUpDto);

        flushAndClearPersistence();

        UserLoginDto loginDto = UserLoginDto.builder()
                .loginId(signUpDto.getLoginId())
                .password(signUpDto.getPassword())
                .build();

        userService.login(loginDto);
    }

    @Test
    void 존재하지_않는_아이디_로그인() {
        UserSignUpDto signUpDto = UserSignUpDto.builder()
                .loginId("test-login-id")
                .password("test-password")
                .nickname("test-nickname")
                .address("test-address")
                .build();

        userService.signUp(signUpDto);

        flushAndClearPersistence();

        UserLoginDto loginDto = UserLoginDto.builder()
                .loginId(signUpDto.getLoginId() + "1")
                .password(signUpDto.getPassword())
                .build();

        assertThatThrownBy(() -> userService.login(loginDto))
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    void 패스워드_불일치_테스트() {
        UserSignUpDto signUpDto = UserSignUpDto.builder()
                .loginId("test-login-id")
                .password("test-password")
                .nickname("test-nickname")
                .address("test-address")
                .build();

        userService.signUp(signUpDto);

        flushAndClearPersistence();

        UserLoginDto loginDto = UserLoginDto.builder()
                .loginId(signUpDto.getLoginId())
                .password(signUpDto.getPassword() + "1")
                .build();

        assertThatThrownBy(() -> userService.login(loginDto))
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    void 탈퇴_회원_로그인() {
        UserSignUpDto signUpDto = UserSignUpDto.builder()
                .loginId("test-login-id")
                .password("test-password")
                .nickname("test-nickname")
                .address("test-address")
                .build();

        userService.signUp(signUpDto);

        flushAndClearPersistence();
        User user = getUserByLoginId(signUpDto);
        userService.signOut(user);
        flushAndClearPersistence();

        UserLoginDto loginDto = UserLoginDto.builder()
                .loginId(signUpDto.getLoginId())
                .password(signUpDto.getPassword())
                .build();

        assertThatThrownBy(() -> userService.login(loginDto))
                .isInstanceOf(AuthorizationException.class);
    }

    //endregion

    //region 회원탈퇴 테스트

    @Test
    void 정상회원_탈퇴() {
        UserSignUpDto signUpDto = UserSignUpDto.builder()
                .loginId("test-login-id")
                .password("test-password")
                .nickname("test-nickname")
                .address("test-address")
                .build();

        userService.signUp(signUpDto);

        flushAndClearPersistence();
        User user = getUserByLoginId(signUpDto);
        userService.signOut(user);
        flushAndClearPersistence();


        user = getUserByLoginId(signUpDto);

        assertThat(user.getStatus()).isEqualTo(DELETE);
    }


    //endregion


    //region STUB METHOD

    private User getUserByLoginId(UserSignUpDto signUpDto) {
        return userRepository.findUserByLoginId(signUpDto.getLoginId()).get();
    }

    private void flushAndClearPersistence() {
        em.flush();
        em.clear();
    }

    //endregion
}