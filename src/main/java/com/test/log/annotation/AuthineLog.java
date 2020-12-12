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
     * 请求入参参数
     */
    String[] panamaArgs() default "";

    /**
     * 请求出参参数
     */
    String[] returnArgs() default "";

}
