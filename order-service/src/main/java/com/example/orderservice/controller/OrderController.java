package com.example.orderservice.controller;

import com.example.common.result.BaseResult;
import com.example.common.result.ResultUtil;
import com.example.model.user.pojo.User;
import com.example.serviceClient.service.user.UserFeignClient;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class OrderController {
    @Resource
    private UserFeignClient userFeignClient;

    @GetMapping("/id")
    public BaseResult<User> UserById(@RequestParam Long userId){
        User user = userFeignClient.getUserById(userId);
        return ResultUtil.success(user);
    }
}
