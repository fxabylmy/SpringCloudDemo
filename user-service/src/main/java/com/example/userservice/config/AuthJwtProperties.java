package com.example.userservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "auth.jwt")
@Component
@RefreshScope
public class AuthJwtProperties {
    //是否开启JWT，即注入相关的类对象
    private Boolean enabled = true;

    //JWT 密钥
    private String secret;

    //accessToken 有效时间
    private Long expire;

    //header名称
    private String header;

    /**
     * 用户登录-用户名参数名称
     */
    private String userParamName;
    /**
     * 用户登录-密码参数名称
     */
    private String pwdParamName;

    //是否使用默认的JWTAuthController
    private Boolean useDefaultController = false;

}
