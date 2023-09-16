package hgk.ecommerce.domain.owner.service;

import hgk.ecommerce.domain.common.exceptions.AuthorizationException;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.owner.dto.request.OwnerLoginDto;
import hgk.ecommerce.domain.owner.dto.request.OwnerSignUpDto;
import hgk.ecommerce.domain.owner.repository.OwnerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class OwnerServiceTest {

    @Autowired
    OwnerService ownerService;
    @Autowired
    OwnerRepository ownerRepository;
    @PersistenceContext
    EntityManager em;

    //region 회원가입 테스트

    @Test
    void 정상_회원가입() {
        OwnerSignUpDto ownerSignUpDto = OwnerSignUpDto.builder()
                .loginId("test-owner-login")
                .password("test-password")
                .build();

        ownerService.signUp(ownerSignUpDto);
        flushAndClearPersistence();

        Owner owner = getOwnerByLoginId(ownerSignUpDto);

        assertThat(owner.getLoginId()).isEqualTo(ownerSignUpDto.getLoginId());
        assertThat(owner.getPassword()).isEqualTo(ownerSignUpDto.getPassword());
        assertThat(owner.getCreateDate()).isNotNull();
        assertThat(owner.getModifyDate()).isNotNull();
    }

    @Test
    void 중복_로그인_회원가입() {
        OwnerSignUpDto ownerSignUpDto = OwnerSignUpDto.builder()
                .loginId("test-owner-login")
                .password("test-password")
                .build();

        ownerService.signUp(ownerSignUpDto);
        flushAndClearPersistence();

        OwnerSignUpDto duplicateId = new OwnerSignUpDto(ownerSignUpDto.getLoginId(), ownerSignUpDto.getPassword() + "1");
        assertThatThrownBy(() -> ownerService.signUp(duplicateId))
                .isInstanceOf(AuthorizationException.class);
    }

    //endregion

    //region 로그인 테스트

    @Test
    void 정상_로그인() {
        OwnerSignUpDto ownerSignUpDto = OwnerSignUpDto.builder()
                .loginId("test-owner-login")
                .password("test-password")
                .build();

        ownerService.signUp(ownerSignUpDto);
        flushAndClearPersistence();

        OwnerLoginDto ownerLoginDto = new OwnerLoginDto(ownerSignUpDto.getLoginId(), ownerSignUpDto.getPassword());

        ownerService.login(ownerLoginDto);
    }

    @Test
    void 비밀번호_불일치_로그인() {
        OwnerSignUpDto ownerSignUpDto = OwnerSignUpDto.builder()
                .loginId("test-owner-login")
                .password("test-password")
                .build();

        ownerService.signUp(ownerSignUpDto);
        flushAndClearPersistence();

        OwnerLoginDto ownerLoginDto = new OwnerLoginDto(ownerSignUpDto.getLoginId(), ownerSignUpDto.getPassword() + "1");

        assertThatThrownBy(() -> ownerService.login(ownerLoginDto))
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    void 아이디_불일치_로그인() {
        OwnerSignUpDto ownerSignUpDto = OwnerSignUpDto.builder()
                .loginId("test-owner-login")
                .password("test-password")
                .build();

        ownerService.signUp(ownerSignUpDto);
        flushAndClearPersistence();

        OwnerLoginDto ownerLoginDto = new OwnerLoginDto(ownerSignUpDto.getLoginId() + "1", ownerSignUpDto.getPassword());

        assertThatThrownBy(() -> ownerService.login(ownerLoginDto))
                .isInstanceOf(AuthorizationException.class);
    }

    //endregion

    //region Stub Method

    private Owner getOwnerByLoginId(OwnerSignUpDto ownerSignUpDto) {
        return ownerRepository.findOwnerByLoginId(ownerSignUpDto.getLoginId()).get();
    }

    private void flushAndClearPersistence() {
        em.flush();
        em.clear();
    }

    //endregion
}