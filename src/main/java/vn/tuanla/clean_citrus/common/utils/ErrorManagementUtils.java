package vn.tuanla.clean_citrus.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import vn.tuanla.clean_citrus.domain.dtos.ErrorMessageDTO;
import vn.tuanla.clean_citrus.repository.jpa.ErrorsManagementRepo;
import vn.tuanla.clean_citrus.service.abs.errormanagement.ErrorsManagementService;

import java.util.*;

@Component
public class ErrorManagementUtils {
    private static final Logger logger = LoggerFactory.getLogger(ErrorManagementUtils.class);

    private final MessageSource messageSource;
    private final ErrorsManagementService errorsManagementService;
    private final ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource;

    // Constructor để tiêm MessageSource và ReloadableResourceBundleMessageSource
    public ErrorManagementUtils(MessageSource messageSource, @Lazy ErrorsManagementService errorsManagementService,
                                @Qualifier("validationMessage") ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource) {
        this.messageSource = messageSource;
        this.errorsManagementService = errorsManagementService;
        this.reloadableResourceBundleMessageSource = reloadableResourceBundleMessageSource;
    }

    // Loại bỏ Redis và chỉ sử dụng messageSource để lấy thông báo lỗi
    // Thêm repository để lấy lỗi từ cơ sở dữ liệu

    public String getMessageByCode(String errorCode, Locale locale) {
        String language = locale.getLanguage();  // Lấy ngôn ngữ hiện tại, ví dụ: 'en' hoặc 'vi'

        // Truy vấn thông báo lỗi từ cơ sở dữ liệu
        ErrorMessageDTO errorMessageOpt = errorsManagementService.findByErrorCode(errorCode);
        // Trả về thông báo lỗi tùy thuộc vào ngôn ngữ
        return language.equals("vi") ? errorMessageOpt.getErrorMessageVi() : errorMessageOpt.getErrorMessageEn();
    }


    // Phương thức overload để lấy thông báo theo mặc định locale
    public String getMessageByCode(String errorCode) {
        return this.getMessageByCode(errorCode, LocaleContextHolder.getLocale());
    }
}
