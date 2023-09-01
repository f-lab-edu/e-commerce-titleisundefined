package hgk.ecommerce.domain.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class NoResourceException extends BaseRuntimeException{
    public NoResourceException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
