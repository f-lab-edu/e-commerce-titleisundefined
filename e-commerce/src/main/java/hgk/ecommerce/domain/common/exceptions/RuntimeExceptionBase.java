package hgk.ecommerce.domain.common.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class RuntimeExceptionBase extends RuntimeException {
    private final HttpStatus httpStatus;

    public RuntimeExceptionBase(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
