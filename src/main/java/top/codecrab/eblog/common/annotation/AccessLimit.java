package top.codecrab.eblog.common.annotation;

import java.lang.annotation.*;

/**
 * 防止被恶意访问
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface AccessLimit {
    int seconds();

    int maxCount();
}
