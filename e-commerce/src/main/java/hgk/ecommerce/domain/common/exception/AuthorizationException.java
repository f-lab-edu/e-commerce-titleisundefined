package hgk.ecommerce.domain.common.exception;

import org.springframework.http.HttpStatus;

public class AuthorizationException extends BaseRuntimeException{
    public AuthorizationException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
