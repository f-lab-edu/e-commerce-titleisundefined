package hgk.ecommerce.domain.user.dto.exceptions;

import hgk.ecommerce.domain.common.exception.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class AlreadyExistException extends BaseRuntimeException {
    public AlreadyExistException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
