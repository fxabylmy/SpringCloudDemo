package com.example.userservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.commom.exception.BusinessException;
import com.example.commom.result.ErrorCode;
import com.example.userservice.jwtUtil.JwtTokenUtil;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.model.pojo.User;
import com.example.userservice.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Override
    public Map<String, Object> userLogin(String account, String password) {
        // 2. 加密
        //String encryptPassword = DigestUtils.md5DigestAsHex((password).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        //queryWrapper.eq("password", encryptPassword);
        queryWrapper.eq("password", password);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的Id
        Map<String, Object> tokenMap = jwtTokenUtil
                .generateTokenAndRefreshToken(String.valueOf(user.getId()),user.getAccount());
        return tokenMap;
    }
}
