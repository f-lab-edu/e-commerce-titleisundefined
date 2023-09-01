package hgk.ecommerce.global.utils;

import hgk.ecommerce.domain.common.exception.AuthenticationException;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public final class SessionUtils {
    @Getter
    public enum SessionRole {
        USER("USER-SESSION"), OWNER("OWNER-SESSION");

        private String content;

        SessionRole(String content) {
            this.content = content;
        }
    }

    public static void setSession(HttpSession session, Long value, SessionRole role) {
        session.setAttribute(role.getContent(), value);
    }

    public static Long getSession(HttpSession session, SessionRole role) {
        Object value = session.getAttribute(role.getContent());
        if(value == null) {
            throw new AuthenticationException("로그인 정보가 없습니다.", HttpStatus.BAD_REQUEST);
        }
        return (Long) value;
    }
}
