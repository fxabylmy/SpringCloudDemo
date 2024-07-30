package com.example.userservice.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.exception.BusinessException;
import com.example.common.result.ErrorCode;

import com.example.jwtutil.jwtUtil.JwtTokenUtil;
import com.example.model.user.pojo.User;
import com.example.serviceClient.service.order.OrderFeignClient;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.service.UserService;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.example.common.constant.RabbitMQConstant.DEMO_MESSAGE_EXCHANGE;
import static com.example.common.constant.RabbitMQConstant.DEMO_MESSAGE_SEND_KEY;
import static com.example.common.exception.ThrowUtils.throwIf;
import static com.example.common.result.ErrorCode.LOGOUT_ERROR;
import static com.example.common.result.ErrorCode.SYSTEM_ERROR;

/**
 * 用户服务层
 *
 * @author fxab
 * @date 2024/07/19
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {

    /**
     * jwt令牌工具类
     */
    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private OrderFeignClient orderFeignClient;

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

    /**
     * 发送消息(测试RabbitMQ)
     *
     * @param message 信息
     * @return {@link Boolean}
     */
    @Override
    public Boolean sendMessage(String message) {
        rabbitTemplate.convertAndSend(DEMO_MESSAGE_EXCHANGE,DEMO_MESSAGE_SEND_KEY,message);
        return true;
    }

    @GlobalTransactional
    @Override
    public Boolean seataTest() {
        Long randomStr = RandomUtil.randomLong(5);
        User user = User.builder()
                //.id(randomStr)
                .account("123")
                .password("123")
                .role("111")
                //.createTime(new Date())
                //.updateTime(new Date())
                .isDelete(0)
                .build();
        save(user);
        log.info("user写入完毕");
        //指定分布式事务是否成功
        Boolean isSuccess = true;
        if (!isSuccess){
            System.out.println("测试微服务失败");
            throw new BusinessException(SYSTEM_ERROR);
        }
        orderFeignClient.save(user.getId());
        return true;
    }
}
