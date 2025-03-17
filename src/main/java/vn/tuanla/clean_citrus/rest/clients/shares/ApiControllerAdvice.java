package vn.tuanla.clean_citrus.rest.clients.shares;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;
import vn.tuanla.clean_citrus.common.ApiRequestInfo;
import vn.tuanla.clean_citrus.common.Response;
import vn.tuanla.clean_citrus.common.utils.DataUtils;
import vn.tuanla.clean_citrus.common.utils.ErrorManagementUtils;
import vn.tuanla.clean_citrus.common.utils.ResponseFactory;
import vn.tuanla.clean_citrus.common.utils.ResponseMessage;
import vn.tuanla.clean_citrus.exception.*;
import vn.tuanla.clean_citrus.rest.dto.response.BadRequestResponse;
import vn.tuanla.clean_citrus.rest.dto.response.ValidationErrorResponse;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestControllerAdvice
public class ApiControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ApiControllerAdvice.class);

    private MessageSource messageSource;
    private ObjectMapper objectMapper;

    private final ErrorManagementUtils errorManagementUtils;

    final ResponseFactory responseFactory;

    public ApiControllerAdvice(ErrorManagementUtils errorManagementUtils, ResponseFactory responseFactory) {
        this.errorManagementUtils = errorManagementUtils;
        this.responseFactory = responseFactory;
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletResponse response) {
        response.setStatus(400);
        BindingResult bindingResult = exception.getBindingResult();
        if (bindingResult.getErrorCount() > bindingResult.getFieldErrorCount()) {
            return responseFactory.error(new BadRequestResponse(exception), HttpStatus.BAD_REQUEST, String.valueOf(HttpStatus.BAD_REQUEST.value()));
        } else {
            return responseFactory.error(new ValidationErrorResponse(exception, errorManagementUtils), HttpStatus.BAD_REQUEST, String.valueOf(HttpStatus.BAD_REQUEST.value()));
        }
    }

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request, HttpServletResponse response) {
        //couldNotDelete
        response.setStatus(400);
        Locale locale = LocaleContextHolder.getLocale();
        if ("DELETE".equals(request.getMethod())) {
            return responseFactory.error(new BadRequestResponse(messageSource.getMessage("couldNotDelete", null, locale)), HttpStatus.BAD_REQUEST, String.valueOf(HttpStatus.BAD_REQUEST.value()));
        } else {
            return responseFactory.error(new BadRequestResponse(messageSource.getMessage("dataIntegrityViolated", null, locale)), HttpStatus.BAD_REQUEST, String.valueOf(HttpStatus.BAD_REQUEST.value()));
        }
    }

    @ExceptionHandler(value = ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException e, HttpServletResponse httpServletResponse) {
        ObjectNode node = objectMapper.createObjectNode();
        if (e.getReason() != null) {
            node.put("message", e.getReason());
        }
        return responseFactory.error(node, HttpStatus.INTERNAL_SERVER_ERROR, String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @ExceptionHandler(value = EmptyResultDataAccessException.class)
    public ResponseEntity<?> handleEmptyResultDataAccessException(EmptyResultDataAccessException e, HttpServletResponse response) {
        response.setStatus(404);
        ObjectNode node = objectMapper.createObjectNode();
        node.put("message",
                messageSource.getMessage("resourceNotFound", null, LocaleContextHolder.getLocale()));
        return responseFactory.error(node, HttpStatus.NOT_FOUND, String.valueOf(HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler({Exception.class, NoSuchMessageException.class, UploadException.class})
    public ResponseEntity<?> handleIOException(Exception e, HttpServletResponse response) {
        logger.error(e.getMessage(), e);
        response.setStatus(500);
        ObjectNode node = objectMapper.createObjectNode();
        node.put("message",
                messageSource.getMessage("internalServerError", null, LocaleContextHolder.getLocale()));
        return responseFactory.error(node, HttpStatus.INTERNAL_SERVER_ERROR, String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        ObjectNode node = objectMapper.createObjectNode();
        String msg;
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            msg = messageSource.getMessage("accessDeniedGet", null, LocaleContextHolder.getLocale());
        } else {
            msg = messageSource.getMessage("accessDeniedAction", null, LocaleContextHolder.getLocale());
        }
        node.put("message", msg);
        return responseFactory.error(node, HttpStatus.FORBIDDEN, String.valueOf(HttpStatus.FORBIDDEN.value()));
    }

    @ExceptionHandler(value = {ConstraintViolationExceptionCustom.class})
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationExceptionCustom exception, HttpServletResponse response) {
        logger.error(exception.getMessage());
        response.setStatus(400);
        return responseFactory.error(new vn.tuanla.clean_citrus.exception.ValidationErrorResponse(exception, errorManagementUtils),
                HttpStatus.BAD_REQUEST, String.valueOf(HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(BusinessException e) {
        String[] info = getApiRequestInfo(e);
        HttpHeaders headers = DataUtils.createApiRequestLogHeaders(info[0], info[1]);
        String message = errorManagementUtils.getMessageByCode(e.getErrorCode(), LocaleContextHolder.getLocale());
        logger.error(message, e);
        return new ResponseEntity<>(new Response(e.getErrorCode(), message, e.getTypeField()), headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExportFileException.class)
    public ResponseEntity<?> handleBusinessException(ExportFileException e) {
        String message = errorManagementUtils.getMessageByCode(e.getErrorCode(), LocaleContextHolder.getLocale());
        logger.error(message, e);
        return responseFactory.error(message, HttpStatus.BAD_REQUEST, e.getErrorCode());
    }


    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return responseFactory.error(new ResponseMessage("File too large!"), HttpStatus.BAD_REQUEST, String.valueOf(HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatch(HttpMessageNotReadableException ex, WebRequest request) {
        logger.error(ex.getMessage());
        Throwable cause = ex.getCause();
        List<String> fieldNames = new ArrayList<>();
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException invalidFormatException = (InvalidFormatException) cause;
            for (JsonMappingException.Reference reference : invalidFormatException.getPath()) {
                fieldNames.add(reference.getFieldName());
            }
            return responseFactory.error(messageSource.getMessage("ERROR.TYPE.REQUEST", new Object[]{String.join(", ", fieldNames)}, LocaleContextHolder.getLocale())
                    , HttpStatus.BAD_REQUEST, String.valueOf(HttpStatus.BAD_REQUEST.value()));
        }
        return responseFactory.error(new ResponseMessage("Invalid request body"), HttpStatus.BAD_REQUEST, String.valueOf(HttpStatus.BAD_REQUEST.value()));
    }

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private String[] getApiRequestInfo(Exception exception) {
        String[] info = new String[2];
        // Lấy stack trace của lỗi
        StackTraceElement[] stackTrace = exception.getStackTrace();
        boolean flag = false;
        // Lặp qua các phần tử trong stack trace để tìm controller
        for (StackTraceElement element : stackTrace) {
            try {
                if (flag) break;
                // Kiểm tra xem phần tử có phải là controller không
                if (!DataUtils.isNullOrEmpty(element.getFileName()) && element.getFileName().toUpperCase().contains("CONTROLLER")) {
                    String methodName = element.getMethodName();
                    Class<?> clazz = Class.forName(element.getClassName());
                    if (clazz.getName().toUpperCase().contains("CONTROLLER")) {
                        Method[] methods = clazz.getMethods();
                        // Kiểm tra nếu lớp này có annotation MyCustomAnnotation
                        for (Method method : methods) {
                            if (method.isAnnotationPresent(ApiRequestInfo.class) && method.getName().equals(methodName)) {
                                ApiRequestInfo annotation = method.getAnnotation(ApiRequestInfo.class);
                                // Lấy giá trị từ annotation
                                info[0] = annotation.apiName();
                                info[1] = annotation.apiAction();
                                flag = true;
                                break;
                            }
                        }
                    }
                }
            } catch (ClassNotFoundException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return info;
    }
}
