package com.ht.weichat.utils;

/**
 * Created by  on 17/1/8.
 */
public class ConstantPool {
    /**
     * 服务器对外访问地址（必须包含上下文路径）。
     * 用于把文章图片的相对路径（如 /img/xxx.png）拼接成完整 URL 返回给小程序端。
     *
     * Spring Boot 版本通过 application.yml 的 app.ip 配置（AppProperties 启动时覆盖此值）；
     * 也可直接改这里的默认值。小程序正式环境要求 https，
     * 例如 https://www.example.com/cms。
     */
    public static String IP = "http://localhost:8080/cms";

    /** 后台文章管理列表每页条数 */
    public static final Integer PAGESIZE = 5;

    /** 小程序接口每页返回条数（与小程序端上拉加载逻辑保持一致，请勿单独修改） */
    public static final Integer API_PAGE_SIZE = 15;
}
