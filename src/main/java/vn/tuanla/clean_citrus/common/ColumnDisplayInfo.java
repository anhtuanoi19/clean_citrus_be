package vn.tuanla.clean_citrus.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnDisplayInfo {

    String displayName() default "";

    String displayParent() default "";

    String mapResource() default "";

    String packagePath() default "";

    String sqlSelectName() default "";

    String description() default "";

    boolean isRate() default false; // bắt buộc giá trị phải để kiểu số

    boolean isEmpty() default false; // nếu muốn để rate mà vẫn muốn empty khi null
}
