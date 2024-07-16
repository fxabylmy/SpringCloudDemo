package com.tuorong.config;

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;


/**
 * 网关nacos配置
 *
 * @author fxab
 * @date 2024/07/16
 */
@Data
@Configuration
@RefreshScope
public class GatewayNacosConfig {

    @Value("${gateway.nacos-config:default}")
    private String gatewayTestConfig;
}
