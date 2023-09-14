package hgk.ecommerce.domain.common.advice;

import hgk.ecommerce.domain.common.exceptions.RuntimeExceptionBase;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@org.springframework.web.bind.annotation.RestControllerAdvice
public class RestControllerAdvice {

    @ExceptionHandler(RuntimeExceptionBase.class)
    public ResponseEntity<ErrorReturn> handledException(RuntimeExceptionBase e) {
        return ResponseEntity.status(e.getHttpStatus())
                .body(new ErrorReturn(e));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorReturn> runtimeException(RuntimeException e) {
        log.error("\n[RuntimeException]", e);

        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(new ErrorReturn(INTERNAL_SERVER_ERROR, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorReturn> checkedException(Exception e) {
        log.error("\n[Exception]", e);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(new ErrorReturn(INTERNAL_SERVER_ERROR, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorReturn> exampleResponseValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        e.getAllErrors().forEach(
                c -> errors.put(((FieldError) c).getField(), c.getDefaultMessage())
        );

        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorReturn(BAD_REQUEST, errors));
    }

    @Getter
    static class ErrorReturn {
        private String httpStatus;
        private Object content;

        ErrorReturn(RuntimeExceptionBase e) {
            this.httpStatus = e.getHttpStatus().getReasonPhrase();
            this.content = e.getMessage();
        }

        ErrorReturn(HttpStatus httpStatus, Object content) {
            this.httpStatus = httpStatus.getReasonPhrase();
            this.content = content;
        }
    }
}
