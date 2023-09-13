package hgk.ecommerce.global.utils;

import hgk.ecommerce.domain.common.exceptions.AuthorizationException;
import org.springframework.http.HttpStatus;

public final class PasswordUtils {
    public static void checkPassword(String input, String realPassword) {
        if(!input.equals(realPassword)) {
            throw new AuthorizationException("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
