package com.example.serviceClient.service.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Seata测试用接口
 * @author fxab
 * @date 2024/07/24
 */
@FeignClient(value = "order-service", path = "/api/order/inner")
public interface OrderFeignClient {

    /**
     * @param
     * @return {@link Boolean}
     */
    @PostMapping("/add")
    Boolean save(@RequestParam("userId") Long userId);
}
