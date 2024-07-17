package com.example.userservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "用户登录请求")
public class UserLoginRequest implements Serializable {

    /**
     * 用户帐户
     */
    @Schema(description = "用户帐户")
    @NotNull(message = "账号不能为空")
    private String account;

    /**
     * 用户密码
     */
    @Schema(description = "用户密码")
    @NotNull(message = "密码不能为空")
    private String password;

    private static final long serialVersionUID = 1L;
}
