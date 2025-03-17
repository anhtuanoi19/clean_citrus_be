package vn.tuanla.clean_citrus.common;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponseError {
    private String status;
    private String message;
    private String errorCode;

    private String detailMessage;
    private Object data;

    public ResponseError(String status, String message, String errorCode, String detailMessage) {
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
        this.detailMessage = detailMessage;
    }

    public ResponseError(String status, String errorCode, Object data) {
        this.status = status;
        this.errorCode = errorCode;
        this.data = data;
    }
}

