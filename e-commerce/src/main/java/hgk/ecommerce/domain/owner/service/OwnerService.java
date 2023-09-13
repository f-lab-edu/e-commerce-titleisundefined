package hgk.ecommerce.domain.owner.service;

import hgk.ecommerce.domain.common.exceptions.AuthorizationException;
import hgk.ecommerce.domain.common.service.SessionService;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.owner.dto.request.OwnerLoginDto;
import hgk.ecommerce.domain.owner.dto.request.OwnerSignUpDto;
import hgk.ecommerce.domain.owner.repository.OwnerRepository;
import hgk.ecommerce.global.utils.PasswordUtils;
import hgk.ecommerce.global.utils.SessionUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static hgk.ecommerce.global.utils.PasswordUtils.*;
import static hgk.ecommerce.global.utils.SessionUtils.*;
import static hgk.ecommerce.global.utils.SessionUtils.SessionRole.*;

@Service
@RequiredArgsConstructor
public class OwnerService {
    private final OwnerRepository ownerRepository;
    private final SessionService sessionService;

    @Transactional(readOnly = true)
    public void login(OwnerLoginDto ownerLoginDto) {
        Owner owner = getOwnerByLoginId(ownerLoginDto);
        checkDuplicateId(owner.getLoginId());
        checkPassword(ownerLoginDto.getPassword(), owner.getPassword());

        sessionService.setSession(owner.getId(), OWNER);
    }

    @Transactional
    public void signUp(OwnerSignUpDto ownerSignUpDto) {
        Owner owner = Owner.createOwner(ownerSignUpDto);
        ownerRepository.save(owner);
    }

    //region PRIVATE METHOD

    private Owner getOwnerByLoginId(OwnerLoginDto ownerLoginDto) {
        return ownerRepository.findOwnerByLoginId(ownerLoginDto.getLoginId()).orElseThrow(() -> {
            throw new AuthorizationException("아이디를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
        });
    }

    private void checkDuplicateId(String loginId) {
        if(ownerRepository.existsOwnerByLoginId(loginId)) {
            throw new AuthorizationException("중복된 아이디가 존재합니다.", HttpStatus.BAD_REQUEST);
        }
    }

    //endregion
}
