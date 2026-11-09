package com.ht.weichat.config;

import com.ht.weichat.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.io.File;

/**
 * Web MVC 配置：JSP 视图解析器、登录拦截器、上传图片目录映射
 *
 * @author cz
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AppProperties appProperties;

    /**
     * JSP 视图解析器（与原 web.xml / applicationContext.xml 中的配置一致）
     */
    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    /**
     * 登录拦截：路径规则与原 applicationContext.xml 的 mvc:interceptors 一致，
     * 放行 /user/login
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/item/**", "/banner/**", "/type/**", "/user/**", "/home")
                .excludePathPatterns("/user/login");
    }

    /**
     * 上传图片目录映射到 /img/**。
     * 内嵌容器下 servletContext.getRealPath 指向临时目录且重启即失，
     * 因此图片统一保存到 app.upload-dir（默认 ./uploads），通过 file: 协议对外提供。
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        File dir = new File(appProperties.getUploadDir()).getAbsoluteFile();
        String location = "file:" + dir.getAbsolutePath().replace('\\', '/') + "/";
        registry.addResourceHandler("/img/**").addResourceLocations(location);
    }
}
