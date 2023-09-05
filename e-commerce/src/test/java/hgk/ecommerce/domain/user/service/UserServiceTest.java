package hgk.ecommerce.domain.user.service;

import hgk.ecommerce.domain.common.exception.AuthenticationException;
import hgk.ecommerce.domain.common.exception.AuthorizationException;
import hgk.ecommerce.domain.common.exception.NoResourceException;
import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.domain.user.dto.enums.Status;
import hgk.ecommerce.domain.user.dto.exceptions.AlreadyExistException;
import hgk.ecommerce.domain.user.dto.request.UserLogin;
import hgk.ecommerce.domain.user.dto.request.UserSign;
import hgk.ecommerce.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @PersistenceContext
    EntityManager em;

    @BeforeEach
    void beforeEach() {
        User user = User.createUser(new UserSign("test-user", "test-password", "test-nickname", "test-address"));
        userRepository.save(user);
        em.flush();
        em.clear();
    }

    //region 로그인 테스트
    @Test
    void 정상_로그인() {
        userService.login(new UserLogin("test-user", "test-password"));
    }

    @Test
    void 존재하지않는_아이디 () {
        assertThatThrownBy(() -> userService.login(new UserLogin("test-user1", "test-password")))
                .isInstanceOf(NoResourceException.class);
    }

    @Test
    void 틀린_비밀번호() {
        assertThatThrownBy(() -> userService.login(new UserLogin("test-user", "test-password1")))
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    void 탈퇴회원_로그인() {
        User user = userRepository.findUserByLoginId("test-user").orElseThrow();
        user.deleteUser();
        em.flush();
        em.clear();
        assertThatThrownBy(() -> userService.login(new UserLogin("test-user", "test-password")))
                .isInstanceOf(AuthenticationException.class);
    }
    //endregion

    //region 회원가입 테스트
    @Test
    void 정상회원_가입() {
        UserSign userSign = new UserSign("test-user-1", "test-password-1", "test-nickname-1", "test-address-1");
        userService.signUp(userSign);

        User user = userRepository.findUserByLoginId(userSign.getLoginId()).orElseThrow();

        assertThat(user.getLoginId()).isEqualTo(userSign.getLoginId());
        assertThat(user.getPassword()).isEqualTo(userSign.getPassword());
        assertThat(user.getNickname()).isEqualTo(userSign.getNickname());
        assertThat(user.getAddress()).isEqualTo(userSign.getAddress());
    }

    @Test
    void 중복_아이디_회원가입() {
        UserSign userSign = new UserSign("test-user-1", "test-password-1", "test-nickname-1", "test-address-1");
        userService.signUp(userSign);

        UserSign sameLoginId = new UserSign("test-user-1", "test-password-2", "test-nickname-2", "test-address-1");
        assertThatThrownBy(() -> userService.signUp(sameLoginId))
                .isInstanceOf(AlreadyExistException.class);
    }

    @Test
    void 중복_닉네임_회원가입() {
        UserSign userSign = new UserSign("test-user-1", "test-password-1", "test-nickname-1", "test-address-1");
        userService.signUp(userSign);

        UserSign sameLoginId = new UserSign("test-user-2", "test-password-2", "test-nickname-1", "test-address-1");
        assertThatThrownBy(() -> userService.signUp(sameLoginId))
                .isInstanceOf(AlreadyExistException.class);
    }
    //endregion

    //region 회원탈퇴 테스트
    @Test
    void 회원탈퇴() {
        User user = userRepository.findUserByLoginId("test-user").orElseThrow();
        user.deleteUser();
        em.flush();
        em.clear();

        User deleteUser = userRepository.findUserByLoginId("test-user").orElseThrow();

        assertThat(deleteUser.getStatus()).isEqualTo(Status.DELETED);
    }
    //endregion
}