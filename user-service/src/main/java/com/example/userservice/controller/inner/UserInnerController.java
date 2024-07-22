package com.example.userservice.controller.inner;

import com.example.model.user.pojo.User;
import com.example.serviceClient.service.user.UserFeignClient;
import com.example.userservice.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户内部控制器
 *
 * @author fxab
 * @date 2024/07/22
 */
@RestController
@RequestMapping("/inner")
@Slf4j
public class UserInnerController implements UserFeignClient {

    /**
     * 用户服务
     */
    @Resource
    private UserService userService;

    /**
     * 按id获取用户
     *
     * @param userId 用户id
     * @return {@link User}
     */
    @Override
    public User getUserById(Long userId) {
        User user = userService.getById(userId);
        return user;
    }
}
