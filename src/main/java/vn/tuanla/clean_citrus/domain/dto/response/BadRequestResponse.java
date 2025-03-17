package vn.tuanla.clean_citrus.domain.dto.response;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

public class BadRequestResponse extends BaseBadRequestResponse {

    private String message;

    public BadRequestResponse() {
        code = "BAD_REQUEST";
    }

    public BadRequestResponse(MethodArgumentNotValidException exception) {

        code = "BAD_REQUEST";

        BindingResult bindingResult = exception.getBindingResult();
        List<ObjectError> errors = bindingResult.getAllErrors();
        if (errors.size() > 0) {
            ObjectError e0 = errors.get(0);
            this.message = e0.getDefaultMessage();
        }
    }

    public BadRequestResponse(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
