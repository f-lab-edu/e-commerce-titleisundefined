package hgk.ecommerce.domain.common.dto;

public class ResponseData<T> {
    T data;

    public ResponseData(T data) {
        this.data = data;
    }
}
