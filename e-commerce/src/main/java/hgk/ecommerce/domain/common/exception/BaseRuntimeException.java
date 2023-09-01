package hgk.ecommerce.domain.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseRuntimeException extends RuntimeException{
    private final HttpStatus httpStatus;

    public BaseRuntimeException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
