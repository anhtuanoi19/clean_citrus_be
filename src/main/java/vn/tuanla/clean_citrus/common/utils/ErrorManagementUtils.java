package vn.tuanla.clean_citrus.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class ErrorManagementUtils {
    private static final Logger logger = LoggerFactory.getLogger(ErrorManagementUtils.class);

    final private MessageSource messageSource;
    final private ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource;

    public ErrorManagementUtils(MessageSource messageSource,
                                @Qualifier("validationMessage") ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource) {
        this.messageSource = messageSource;
        this.reloadableResourceBundleMessageSource = reloadableResourceBundleMessageSource;
    }

    public String getMessageByCode(String errorCode, Locale locale) {
        String message;
        try {
            // Truy xuất thông báo từ messageSource hoặc validationMessage nếu có
            try {
                message = this.messageSource.getMessage(errorCode, null, locale); // khong co trong message thi lay o validation
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                // Nếu không tìm thấy thông báo từ messageSource thì thử lấy từ reloadableResourceBundleMessageSource
                message = this.reloadableResourceBundleMessageSource.getMessage(errorCode, null, locale);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            message = "Message not found from code: " + errorCode;
        }
        return message;
    }

    public String getMessageByCode(String errorCode) {
        return this.getMessageByCode(errorCode, LocaleContextHolder.getLocale());
    }
}
