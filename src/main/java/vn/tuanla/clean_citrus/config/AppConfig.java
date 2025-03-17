package vn.tuanla.clean_citrus.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import vn.tuanla.clean_citrus.i18n.PreferVietnameseAcceptHeaderLocaleResolver;

import java.util.Locale;
import java.util.concurrent.Executor;


@Configuration
@EnableAsync
@EnableScheduling
@EnableJpaAuditing
public class AppConfig implements AsyncConfigurer {

    @Bean(destroyMethod = "shutdown")
    @Primary
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int processors = Runtime.getRuntime().availableProcessors();
        executor.setMaxPoolSize(processors);
        executor.setCorePoolSize((int) Math.ceil(processors / 3d));
        executor.setKeepAliveSeconds(120);
        executor.setThreadNamePrefix("task-");
        return executor;
    }

    /* Methods from AsyncConfigurer */
    @Override
    public Executor getAsyncExecutor() {
        return taskExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
    /* End Methods from AsyncConfigurer */

    @Bean
    public LocaleResolver localeResolver() {
        return new PreferVietnameseAcceptHeaderLocaleResolver();
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new SpringSecurityAuditorAware();
    }

    @Bean
    @Primary
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        factory.setValidationMessageSource(validationMessage());
        return factory;
    }

    @Bean
    public ReloadableResourceBundleMessageSource validationMessage() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("validation");
        messageSource.setDefaultEncoding("UTF-8");
        // Load a key to trigger resource bundle loading
        return messageSource;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Đăng ký module custom để trim chuỗi
        mapper.registerModule(new StringTrimModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}
