package com.example.userservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.commom.exception.BusinessException;
import com.example.commom.result.ErrorCode;

import com.example.commom.result.ResultUtil;
import com.example.jwtutil.jwtUtil.JwtTokenUtil;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.model.pojo.User;
import com.example.userservice.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.example.commom.exception.ThrowUtils.throwIf;
import static com.example.commom.result.ErrorCode.LOGOUT_ERROR;
import static com.example.commom.result.ErrorCode.TOKEN_INVALID;

/**
 * 用户服务层
 *
 * @author fxab
 * @date 2024/07/19
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * jwt令牌工具类
     */
    @Resource
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 用户登录
     *
     * @param account  帐户
     * @param password 密码
     * @return {@link Map}<{@link String}, {@link Object}>
     */
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

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    @Override
    public Map<String, Object> refreshToken(String refreshToken) {
        //未获取到token
        throwIf(StringUtils.isEmpty(refreshToken),ErrorCode.TOKEN_MISSION);
        //token无效或过期
        throwIf(jwtTokenUtil.isTokenExpired(refreshToken),ErrorCode.TOKEN_INVALID);
        String userId = jwtTokenUtil.getUserIdFromToken(refreshToken);
        String username = jwtTokenUtil.getUserNameFromToken(refreshToken);
        //判断令牌是否在redis中
        throwIf(jwtTokenUtil.isRefreshTokenNotExistCache(refreshToken),ErrorCode.TOKEN_INVALID);
        //刷新token，refresh仅使用一次，用完即删除
        Map<String, Object> tokenMap = jwtTokenUtil.refreshTokenAndGenerateToken(userId, username);
        return tokenMap;
    }

    /**
     * 注销
     *
     * @param userId 用户id
     * @return {@link Boolean}
     */
    @Override
    public Boolean logout(String userId) {
        Boolean logoutResult = jwtTokenUtil.removeToken(userId);
        throwIf(!logoutResult,LOGOUT_ERROR);
        return true;
    }
}
