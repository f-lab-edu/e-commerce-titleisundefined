package hgk.ecommerce.domain.common.exceptions;

import org.springframework.http.HttpStatus;

public class DuplicatedException extends RuntimeExceptionBase{
    public DuplicatedException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
