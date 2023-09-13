package hgk.ecommerce.domain.common.exceptions;

import org.springframework.http.HttpStatus;

public class AuthorizationException extends RuntimeExceptionBase{
    public AuthorizationException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
