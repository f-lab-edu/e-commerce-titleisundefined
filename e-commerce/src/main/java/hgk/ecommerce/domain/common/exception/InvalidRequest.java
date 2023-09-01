package hgk.ecommerce.domain.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidRequest extends BaseRuntimeException{
    public InvalidRequest(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
