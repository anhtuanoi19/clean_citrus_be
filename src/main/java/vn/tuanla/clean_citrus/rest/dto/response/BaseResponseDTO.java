package vn.tuanla.clean_citrus.rest.dto.response;

public class BaseResponseDTO {
    private String message = "Success";
    private Integer status = 200;
    private Object data;

    public BaseResponseDTO(String message, Integer status, Object data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public BaseResponseDTO(String message, Integer status) {
        this.message = message;
        this.status = status;
    }

    public BaseResponseDTO() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Object getData() { return data; }

    public void setData(Object data) { this.data = data; }
}
