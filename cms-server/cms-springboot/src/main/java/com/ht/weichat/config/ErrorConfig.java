package com.ht.weichat.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * 错误页注册：404 / 500 / Throwable 统一转发到 /error，
 * 由 BootErrorController 渲染 /WEB-INF/jsp/error.jsp
 *
 * @author cz
 */
@Configuration
public class ErrorConfig implements ErrorPageRegistrar {

    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        registry.addErrorPages(
                new ErrorPage(HttpStatus.NOT_FOUND, "/error"),
                new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error"),
                new ErrorPage(Throwable.class, "/error"));
    }
}
