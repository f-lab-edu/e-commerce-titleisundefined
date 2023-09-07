package hgk.ecommerce.global.advice;

import hgk.ecommerce.domain.common.exception.BaseRuntimeException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@org.springframework.web.bind.annotation.RestControllerAdvice
public class RestControllerAdvice {

    @ExceptionHandler(BaseRuntimeException.class)
    public ResponseEntity<ErrorReturn> myRuntimeException(BaseRuntimeException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(new ErrorReturn(e));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorReturn> runtimeException(RuntimeException e) {
        log.error("\n[RuntimeException]", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorReturn(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorReturn> checkedException(Exception e) {
        log.error("\n[Exception]", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorReturn(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
    }


    @Getter
    static class ErrorReturn {
        private String reason;
        private String message;

        ErrorReturn(BaseRuntimeException e) {
            this.reason = e.getHttpStatus().getReasonPhrase();
            this.message = e.getMessage();
        }

        ErrorReturn(HttpStatus status, String message) {
            this.reason = status.getReasonPhrase();
            this.message = message;
        }
    }
}
