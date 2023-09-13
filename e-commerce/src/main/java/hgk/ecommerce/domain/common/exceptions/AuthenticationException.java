package hgk.ecommerce.domain.common.exceptions;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends RuntimeExceptionBase{
    public AuthenticationException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
