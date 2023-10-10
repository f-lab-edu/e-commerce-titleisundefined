package hgk.ecommerce.domain.common.service;

import hgk.ecommerce.domain.common.exceptions.AuthorizationException;
import hgk.ecommerce.domain.owner.repository.OwnerRepository;
import hgk.ecommerce.domain.user.repository.UserRepository;
import hgk.ecommerce.global.utils.SessionUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static hgk.ecommerce.global.utils.SessionUtils.SessionRole.*;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final HttpSession httpSession;

    public Long getCurrentOwnerId() {
        Long ownerId = getSessionValue(OWNER);

        if(ownerId == null) {
            throw new AuthorizationException("로그인 정보가 없습니다.", HttpStatus.BAD_REQUEST);
        }

        return ownerId;
    }

    public Long getCurrentUserId() {
        Long userId = getSessionValue(USER);

        if(userId == null) {
            throw new AuthorizationException("로그인 정보가 없습니다.", HttpStatus.BAD_REQUEST);
        }
        return userId;
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
