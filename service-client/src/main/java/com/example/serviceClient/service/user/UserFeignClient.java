package com.example.serviceClient.service.user;

import com.example.model.user.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户服务Feign客户端
 *
 * @author fxab
 * @date 2024/07/22
 */
@FeignClient(value = "user-service", path = "/api/user/inner")
public interface UserFeignClient {

    /**
     * 按id获取用户
     *
     * @param userId 用户id
     * @return {@link User}
     */
    @GetMapping("/get/id")
    User getUserById(@RequestParam("userId") Long userId);
}
