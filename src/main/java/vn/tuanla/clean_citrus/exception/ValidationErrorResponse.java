package vn.tuanla.clean_citrus.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import vn.tuanla.clean_citrus.common.utils.ErrorManagementUtils;
import vn.tuanla.clean_citrus.domain.dto.response.BaseBadRequestResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationErrorResponse extends BaseBadRequestResponse {

    private String message;
    private List<Violation> violations;

    private void setCode() {
        code = "VALIDATION_ERROR";
    }

    public ValidationErrorResponse(ConstraintViolationException ex, ErrorManagementUtils errorManagementUtils) {
        setCode();
        violations = new ArrayList<>();
        Set<ConstraintViolation<?>> constraintsViolations = ex.getConstraintViolations();
        for (ConstraintViolation<?> cv : constraintsViolations) {
            String msg = cv.getMessage();
            message = errorManagementUtils.getMessageByCode(msg != null ? msg.replaceAll("\\{", "").replaceAll("\\}", "") : "");
            String fieldName = cv.getPropertyPath().toString();
            Violation v = new Violation(fieldName, message);
            violations.add(v);
        }
    }


    public ValidationErrorResponse(MethodArgumentNotValidException exception) {
        setCode();
        this.violations = new ArrayList<>();
        BindingResult bindingResult = exception.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        int errorCount = bindingResult.getErrorCount();
        System.out.println("..............errorCount: " + errorCount);
        if (errorCount > fieldErrors.size()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            for (ObjectError error : errors) {
                System.out.println("Error class: " + error.getClass().getName() + ": " + error.getDefaultMessage());
            }
        } else {
            int i = 0;
            for (FieldError fe : fieldErrors) {
                this.violations.add(new Violation(fe));
                if (i == 0) {
                    message = fe.getDefaultMessage();
                }
                i++;
            }
        }
    }


    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public void setViolations(List<Violation> violations) {
        this.violations = violations;
    }

    public class Violation {
        private String fieldName;
        private String errorMessage;

        Violation(FieldError fieldError) {
            this(fieldError.getField(), fieldError.getDefaultMessage());
        }

        Violation(String fieldName, String errorMessage) {
            this.fieldName = fieldName;
            this.errorMessage = errorMessage;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}

