package hgk.ecommerce.domain.common.service;

import hgk.ecommerce.domain.common.exceptions.AuthorizationException;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.owner.repository.OwnerRepository;
import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.domain.user.repository.UserRepository;
import hgk.ecommerce.global.utils.SessionUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static hgk.ecommerce.global.utils.SessionUtils.SessionRole.*;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final OwnerRepository ownerRepository;
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Transactional(readOnly = true)
    public Owner getCurrentOwner() {
        Long ownerId = SessionUtils.getSession(httpSession, OWNER);

        return ownerRepository.findById(ownerId).orElseThrow(() -> {
            throw new AuthorizationException("로그인 정보가 없습니다.", HttpStatus.BAD_REQUEST);
        });
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Long ownerId = SessionUtils.getSession(httpSession, USER);

        return userRepository.findById(ownerId).orElseThrow(() -> {
            throw new AuthorizationException("로그인 정보가 없습니다.", HttpStatus.BAD_REQUEST);
        });
    }

    public void logout() {
        httpSession.invalidate();
    }

    public void setSession(Long value, SessionUtils.SessionRole role) {
        SessionUtils.setSession(httpSession, value, role);
    }

    public Long getSessionValue(SessionUtils.SessionRole role) {
        return SessionUtils.getSession(httpSession, role);
    }
}
