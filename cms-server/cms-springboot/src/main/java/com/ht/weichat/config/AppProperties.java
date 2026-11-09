package com.ht.weichat.config;

import com.ht.weichat.utils.ConstantPool;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 应用级配置项（application.yml 的 app.* 前缀）
 *
 * @author cz
 */
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /**
     * 服务端对外地址（含上下文路径），用于拼接图片完整 URL 返回给小程序端
     */
    private String ip = "http://localhost:8080/cms";

    /**
     * 图片上传目录（相对路径基于运行目录；内嵌容器下不再写入 war 内部，重启不丢图）
     */
    private String uploadDir = "uploads";

    @PostConstruct
    public void syncToConstantPool() {
        // 业务代码沿用 ConstantPool.IP 读取图片地址前缀，启动时用配置覆盖
        if (ip != null && !ip.trim().isEmpty()) {
            ConstantPool.IP = ip.trim();
        }
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}
