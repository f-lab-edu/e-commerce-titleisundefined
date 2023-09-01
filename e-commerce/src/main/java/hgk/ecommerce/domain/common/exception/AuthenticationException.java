package hgk.ecommerce.domain.common.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends BaseRuntimeException{
    public AuthenticationException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
