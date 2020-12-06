package com.test.log.configuration;

        import com.test.log.aop.AuthineLogAop;
        import org.springframework.context.annotation.Configuration;
        import org.springframework.context.annotation.Import;

@Configuration
@Import({AuthineLogAop.class})
public class AuthineLogAutoConfiguration {
}
