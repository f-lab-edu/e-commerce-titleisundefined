package hgk.ecommerce.domain.common.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidRequest extends RuntimeExceptionBase{
    public InvalidRequest(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
