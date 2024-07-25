package com.example.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.model.user.pojo.User;

import java.util.Map;


public interface UserService extends IService<User> {

    Map<String, Object> userLogin(String account, String password);

    Map<String, Object> refreshToken(String refreshToken);

    Boolean logout(String userId);

    Boolean sendMessage(String message);

    Boolean seataTest();
}
