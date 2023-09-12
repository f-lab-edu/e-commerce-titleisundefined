package hgk.ecommerce.domain.common.exception;

import org.springframework.http.HttpStatus;

public class InternalServerException extends BaseRuntimeException{
    public InternalServerException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
