package com.test.log.aop;

import com.test.log.annotation.AspectSupportUtils;
import com.test.log.annotation.AuthineLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;


@Component
@Aspect
@Slf4j
public class AuthineLogAop {

    //执行时间ThreadLocal
    ThreadLocal<Long> excuseTimeThreadLocal = new ThreadLocal<>();


    @Pointcut("@annotation(com.test.log.annotation.AuthineLog)")
    private void logRecord() {}//定义一个切入点

    //前置通知
    @Before("logRecord()")
    public void beforeAdvice(JoinPoint jp) {
        excuseTimeThreadLocal.set(System.currentTimeMillis());
        log.info("===========before advice");

    }


    @AfterThrowing("logRecord()")
    public void onException(JoinPoint jp) {
        Long exclutTime = System.currentTimeMillis() - excuseTimeThreadLocal.get();
        log.info("失败！！！========异常了执行时间:{}", exclutTime);
        MethodSignature signature = (MethodSignature) jp.getSignature();
        if (null != signature) {
            Method method = signature.getMethod();
            if (null != method && method.isAnnotationPresent(AuthineLog.class)) {
                AuthineLog authineLog = method.getAnnotation(AuthineLog.class);
                //描述
                String desc = authineLog.desc();
                //应用名称el
                String appNameEl = authineLog.appNameEl();
                //使用spring EL表达式获取参数中的值
                try {
                    String appName = (String) AspectSupportUtils.getKeyValue(jp, appNameEl);
                    log.info("注解appNameEl={}解析出的值appName={}", appNameEl, appName);
                } catch (Exception e) {
                    log.error("appNameEl={}解析失败", appNameEl);
                    e.printStackTrace();
                }
                /// TODO

            }
        }
    }




    @AfterReturning(pointcut = "logRecord()", returning = "result")
    public void determine(JoinPoint jp, Object result) {
        Long exclutTime = System.currentTimeMillis() - excuseTimeThreadLocal.get();
        System.out.println("执行成功：" + exclutTime);
        MethodSignature signature = (MethodSignature) jp.getSignature();
        if (null != signature) {
            Method method = signature.getMethod();
            if (null != method && method.isAnnotationPresent(AuthineLog.class)) {
                AuthineLog authineLog = method.getAnnotation(AuthineLog.class);
                //描述
                String desc = authineLog.desc();
                //应用名称el
                String appNameEl = authineLog.appNameEl();
                //使用spring EL表达式获取参数中的值
                try {
                    String appName = (String) AspectSupportUtils.getKeyValue(jp, appNameEl);
                    log.info("注解appNameEl={}解析出的值appName={}", appNameEl, appName);
                } catch (Exception e) {
                    log.error("appNameEl={}解析失败", appNameEl);
                    e.printStackTrace();
                }
                /// TODO

            }
        }
    }


}
