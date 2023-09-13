package hgk.ecommerce.domain.common.exceptions;

import org.springframework.http.HttpStatus;

public class NoResourceException extends RuntimeExceptionBase{
    public NoResourceException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
