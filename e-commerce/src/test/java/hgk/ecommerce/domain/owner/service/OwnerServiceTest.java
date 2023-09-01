package hgk.ecommerce.domain.owner.service;

import hgk.ecommerce.domain.common.exception.AuthorizationException;
import hgk.ecommerce.domain.common.exception.NoResourceException;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.owner.dto.OwnerLogin;
import hgk.ecommerce.domain.owner.dto.OwnerSign;
import hgk.ecommerce.domain.owner.repository.OwnerRepository;
import hgk.ecommerce.domain.user.dto.exceptions.AlreadyExistException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OwnerServiceTest {
    @Autowired
    OwnerService ownerService;
    @Autowired
    OwnerRepository ownerRepository;

    @PersistenceContext
    EntityManager em;

    //region 회원가입
    @Test
    void 정상_회원가입() {
        OwnerSign ownerSign = new OwnerSign("test-owner", "test-password");
        ownerService.signUp(ownerSign);

        em.flush();
        em.clear();

        Owner owner = ownerRepository.findOwnerByLoginId(ownerSign.getLoginId()).orElseThrow();

        assertThat(owner.getLoginId()).isEqualTo(ownerSign.getLoginId());
        assertThat(owner.getPassword()).isEqualTo(ownerSign.getPassword());
    }

    @Test
    void 중복_아이디_회원가입() {
        OwnerSign ownerSign = new OwnerSign("test-owner", "test-password");
        ownerService.signUp(ownerSign);
        em.flush();
        em.clear();
        OwnerSign newOwnerSign = new OwnerSign("test-owner", "test-password2");
        assertThatThrownBy(() -> ownerService.signUp(newOwnerSign))
                .isInstanceOf(AlreadyExistException.class);

    }
    //endregion

    //region 로그인
    @Test
    void 정상_로그인() {
        OwnerSign ownerSign = new OwnerSign("test-owner", "test-password");
        ownerService.signUp(ownerSign);

        em.flush();
        em.clear();
        OwnerLogin ownerLogin = new OwnerLogin(ownerSign.getLoginId(), ownerSign.getPassword());
        ownerService.login(ownerLogin);
    }

    @Test
    void 아이디_불일치_로그인() {
        OwnerSign ownerSign = new OwnerSign("test-owner", "test-password");
        ownerService.signUp(ownerSign);

        em.flush();
        em.clear();
        OwnerLogin ownerLogin = new OwnerLogin(ownerSign.getLoginId() + "1" , ownerSign.getPassword());
        assertThatThrownBy(() -> ownerService.login(ownerLogin))
                .isInstanceOf(NoResourceException.class);
    }

    @Test
    void 비밀번호_불일치_로그인() {
        OwnerSign ownerSign = new OwnerSign("test-owner", "test-password");
        ownerService.signUp(ownerSign);

        em.flush();
        em.clear();
        OwnerLogin ownerLogin = new OwnerLogin(ownerSign.getLoginId() , ownerSign.getPassword() + "1");
        assertThatThrownBy(() -> ownerService.login(ownerLogin))
                .isInstanceOf(AuthorizationException.class);
    }
    //endregion

}