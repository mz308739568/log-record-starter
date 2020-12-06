package com.test.log.annotation;


import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthineLog {

    /**
     * 描述
     **/
     String desc() default "";

    /**
     * 应用名称表达式
     */
    String appNameEl() default "";

}
