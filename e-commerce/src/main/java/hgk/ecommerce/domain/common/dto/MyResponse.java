package hgk.ecommerce.domain.common.dto;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public final class MyResponse {
    public static ResponseEntity RESPONSE_OK = new ResponseEntity<>(HttpStatusCode.valueOf(200));

    public static <T> ResponseData<T> test(T data)  {
        return new ResponseData<T>(data);
    }
}
