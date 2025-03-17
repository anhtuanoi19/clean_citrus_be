package vn.tuanla.clean_citrus.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessException extends RuntimeException {
    private String errorCode;
    private String typeField;

    public BusinessException(String errorCode) {
        super();
        this.errorCode = errorCode;
    }
    public BusinessException(String errorCode, String typeField) {
        super();
        this.errorCode = errorCode;
        this.typeField = typeField;

    }
}
