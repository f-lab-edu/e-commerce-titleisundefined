package hgk.ecommerce.domain.owner.service;

import hgk.ecommerce.domain.common.exception.AuthenticationException;
import hgk.ecommerce.domain.common.exception.AuthorizationException;
import hgk.ecommerce.domain.common.exception.NoResourceException;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.owner.dto.OwnerLogin;
import hgk.ecommerce.domain.owner.dto.OwnerSign;
import hgk.ecommerce.domain.owner.repository.OwnerRepository;
import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.domain.user.dto.enums.Status;
import hgk.ecommerce.domain.user.dto.exceptions.AlreadyExistException;
import hgk.ecommerce.domain.user.dto.request.UserLogin;
import hgk.ecommerce.domain.user.dto.request.UserSign;
import hgk.ecommerce.global.utils.SessionUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static hgk.ecommerce.global.utils.SessionUtils.SessionRole.OWNER;
import static hgk.ecommerce.global.utils.SessionUtils.SessionRole.USER;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class OwnerService {
    private final OwnerRepository ownerRepository;
    private final HttpSession httpSession;

    @Transactional(readOnly = true)
    public void login(OwnerLogin ownerLogin) {
        Owner owner = ownerRepository.findOwnerByLoginId(ownerLogin.getLoginId()).orElseThrow(() -> {
            throw new NoResourceException("존재하지 않는 아이디입니다.", BAD_REQUEST);
        });

        checkPassword(ownerLogin.getPassword(), owner.getPassword());

        SessionUtils.setSession(httpSession, owner.getId(), OWNER);
    }

    @Transactional
    public void signUp(OwnerSign ownerSign) {
        checkDuplicateLoginId(ownerSign.getLoginId());

        Owner owner = Owner.createOwner(ownerSign);

        ownerRepository.save(owner);
    }

    //region PRIVATE METHOD
    private void checkDuplicateLoginId(String loginId) {
        if (ownerRepository.existsOwnerByLoginId(loginId) == true) {
            throw new AlreadyExistException("이미 존재하는 아이디 입니다.", BAD_REQUEST);
        }
    }

    private void checkPassword(String plainText, String password) {
        if(!plainText.equals(password)) {
            throw new AuthorizationException("비밀번호가 일치하지 않습니다.", BAD_REQUEST);
        }
    }
    //endregion
}
