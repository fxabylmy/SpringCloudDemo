package com.example.userservice.controller;

import com.example.commom.result.BaseResult;
import com.example.commom.result.ResultUtil;
import com.example.userservice.model.dto.UserLoginRequest;
import com.example.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RefreshScope // 自动配置更新
@RestController
@RequestMapping("/")
@Slf4j
@Tag(name = "用户接口文档", description = "用户中心模块接口文档")
public class UserController {

    @Resource
    private UserService userService;

    @Operation(summary = "用户统一登录接口")
    @PostMapping("/login")
    public BaseResult<Map<String, Object>> Login(@RequestBody UserLoginRequest userLoginRequest){
        String account = userLoginRequest.getAccount();
        String password = userLoginRequest.getPassword();
        Map<String, Object> tokenMap = userService.userLogin(account,password);
        return ResultUtil.success(tokenMap);
    }

}
