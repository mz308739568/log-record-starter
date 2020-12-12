package com.test.log.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.test.log.annotation.AspectSupportUtils;
import com.test.log.annotation.AuthineLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
@Aspect
@Slf4j
public class AuthineLogAop {

    //执行时间ThreadLocal
    ThreadLocal<Long> excuseTimeThreadLocal = new ThreadLocal<>();


    @Pointcut("@annotation(com.test.log.annotation.AuthineLog)")
    private void logRecord() {
    }//定义一个切入点

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
    }


    @AfterReturning(pointcut = "logRecord()", returning = "result")
    public void determine(JoinPoint jp, Object result) {
        Long exclutTime = System.currentTimeMillis() - excuseTimeThreadLocal.get();
        System.out.println("执行成功：" + exclutTime);
        MethodSignature signature = (MethodSignature) jp.getSignature();
        if (null != signature) {
            Method method = signature.getMethod();
            AuthineLog authineLog = method.getAnnotation(AuthineLog.class);
            //描述
            String desc = authineLog.desc();
            //应用名称el
            String[] appNameEl = authineLog.panamaArgs();
            //使用spring EL表达式获取参数中的值
            try {
                List<String> values = getValues(jp, authineLog, result);
                desc = String.format(desc, values.toArray());
            } catch (Exception e) {
                log.error("appNameEl={}解析失败", appNameEl);
                e.printStackTrace();
            }
            /// TODO

        }
    }


    /**
     * @return java.util.List<java.lang.String>
     * @Author mengz
     * @Description 根据key解析获取参数值
     * @Date 19:54 2020/12/8
     * @Param [jp, authineLog]
     **/
    private List<String> getValues(JoinPoint jp, AuthineLog authineLog, Object result) {
        //解析入参参数
        String[] panamaArgs = authineLog.panamaArgs();
        //使用spring EL表达式获取参数中的值并对描述进行替换
        List<String> values = new ArrayList<>();
        if (panamaArgs.length > 0) {
            for (int i = 0; i < panamaArgs.length; i++) {
                if (!StringUtils.isEmpty(panamaArgs[i])) {
                    String argValue = (String) AspectSupportUtils.getKeyValue(jp, panamaArgs[i]);
//                    log.info("注解panamaArgKey={}解析出的值value={}", panamaArgs[i], argValue);
                    values.add(argValue);
                }
            }
        }
        //解析返回结果参数
        String[] returnArgs = authineLog.returnArgs();
        if (!StringUtils.isEmpty(returnArgs[0]) && returnArgs.length > 0) {
            Map<String, Object> map = null;
//            if (!(result instanceof SingleResponse)) {
//                //添加空参数防止替换时报错
//                setValues(values, returnArgs);
//                return values;
//            }
            try {
                map = JSONObject.parseObject(JSON.toJSONString(result));
            } catch (Exception e) {
            }
            for (int i = 0; i < returnArgs.length; i++) {
                String[] keys = returnArgs[i].split("\\.");
                if (keys.length == 1 && !StringUtils.isEmpty(keys[0])) {
                    values.add(result.toString());
                } else {
                    for (int j = 1; j < keys.length; j++) {
                        String key = keys[j];
                        if (!StringUtils.isEmpty(key) && null != map && (j + 1) != keys.length) {
                            map = (Map<String, Object>) map.get(key);
                        } else {
                            Object argValue = map.get(key);
//                            log.info("注解returnArgKey={}解析出的值value={}", returnArgs[i], argValue);
                            values.add(argValue == null ? "" : argValue.toString());
                        }
                    }
                }
            }
        }
        return values;
    }


}
