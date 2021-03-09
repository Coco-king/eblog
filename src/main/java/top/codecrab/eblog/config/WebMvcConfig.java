package top.codecrab.eblog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.codecrab.eblog.common.interceptor.SessionInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    Consts consts;

    //防止恶意访问
    @Autowired
    private SessionInterceptor sessionInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/avatar/**", "/upload/post/**")
                .addResourceLocations("file:///" + consts.getUploadDir() + "/avatar/", "file:///" + consts.getUploadDir() + "/post/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加拦截器
        InterceptorRegistration registration = registry.addInterceptor(sessionInterceptor);
        //将这个controller放行
        registration.excludePathPatterns("/error");
        //拦截全部
        registration.addPathPatterns("/**");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
