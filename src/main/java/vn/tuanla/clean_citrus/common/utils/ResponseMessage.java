package vn.tuanla.clean_citrus.common.utils;

import lombok.Data;

@Data

public class ResponseMessage {
    private String message;

    public ResponseMessage(String message) {
        this.message = message;
    }

}
