package com.example.cms.config;

import com.example.cms.admin.AdminAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 설정.
 *
 * <p>레거시의 dispatcher-servlet.xml 에서 XML 로 하던 인터셉터 등록을
 * 자바 설정으로 대체한 것.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdminAuthInterceptor())
                .order(1)
                .addPathPatterns("/admin/**")        // 관리자 영역 전체 보호
                .excludePathPatterns("/admin/login"); // 로그인 화면/처리는 예외
    }
}
