package com.example.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.userservice.model.pojo.User;

import java.util.Map;


public interface UserService extends IService<User> {

    Map<String, Object> userLogin(String account, String password);
}
