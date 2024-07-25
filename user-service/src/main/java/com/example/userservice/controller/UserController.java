package com.example.userservice.controller;

import com.example.commom.result.BaseResult;
import com.example.commom.result.ResultUtil;
import com.example.model.user.dto.UserLoginRequest;
import com.example.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.example.commom.result.ErrorCode.SYSTEM_ERROR;

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


    @Operation(summary = "令牌刷新")
    @PostMapping("/token/refresh")
    public BaseResult<Map<String, Object>> RefreshToken(@RequestHeader(value = "${auth.jwt.header}") String refreshToken){
        Map<String, Object> tokenMap = userService.refreshToken(refreshToken);
        return ResultUtil.success(tokenMap);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public BaseResult<Map<String, Object>> Logout(String userId){
        Boolean logoutResult = userService.logout(userId);
        if (logoutResult){
            return ResultUtil.success();
        }
        return ResultUtil.error(SYSTEM_ERROR);
    }


    /**
     * 发送消息(RabbitMQ测试)
     *
     * @param message 信息
     * @return {@link BaseResult}<{@link String}>
     */
    @PostMapping("/message")
    public BaseResult<String> sendMessage(String message){
        Boolean isSuccess = userService.sendMessage(message);
        if (isSuccess){
            return ResultUtil.success();
        }
        return ResultUtil.error(SYSTEM_ERROR);
    }

    /**
     * 添加用户和订单(测试分布式事务)
     *
     * @return {@link BaseResult}<{@link Boolean}>
     */
    @PostMapping("/add")
    public BaseResult<Boolean> addUserAndOrder(){
        Boolean isSuccess = userService.seataTest();
        if (isSuccess){
            return ResultUtil.success();
        }
        return ResultUtil.error(SYSTEM_ERROR);
    }
}
