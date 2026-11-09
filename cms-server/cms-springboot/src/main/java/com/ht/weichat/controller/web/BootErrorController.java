package com.ht.weichat.controller.web;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 统一错误入口：渲染 /WEB-INF/jsp/error.jsp
 * （error.jsp 读取 javax.servlet.error.status_code 区分 404 / 500）
 *
 * @author cz
 */
@Controller
public class BootErrorController implements ErrorController {

    @RequestMapping("/error")
    public String error() {
        return "error";
    }
}
