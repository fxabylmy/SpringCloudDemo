package com.example.filter;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.common.result.BaseResult;
import com.example.common.result.ErrorCode;
import com.example.jwtutil.config.AuthJwtProperties;
import com.example.jwtutil.jwtUtil.JwtTokenUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static com.example.common.result.ErrorCode.TOKEN_INVALID;
import static com.example.common.result.ErrorCode.TOKEN_MISSION;

@Configuration
@Slf4j
public class AuthFilter {
    private static final String AUTH_TOKEN_URL = "/api/user/login";
    private static final String REFRESH_TOKEN_URL = "/api/user/token/refresh";
    public static final String USER_ID = "userId";
    public static final String USER_NAME = "username";
    public static final String FROM_SOURCE = "from-source";

    @Resource
    private AuthJwtProperties authJwtProperties;

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Bean
    @Order(-101)
    public GlobalFilter jwtAuthGlobalFilter(){
        return (exchange, chain) -> {
            //登陆判断
            ServerHttpRequest serverHttpRequest = exchange.getRequest();
            ServerHttpResponse serverHttpResponse = exchange.getResponse();
            ServerHttpRequest.Builder mutate = serverHttpRequest.mutate();
            String requestUrl = serverHttpRequest.getURI().getPath();
            // 跳过对登录请求的 token 检查。因为登录请求是没有 token 的，是来申请 token 的。
            if(AUTH_TOKEN_URL.equals(requestUrl)||REFRESH_TOKEN_URL.equals(requestUrl)) {
                return chain.filter(exchange);
            }

            // 从 HTTP 请求头中获取 JWT 令牌
            String token = getToken(serverHttpRequest);
            if (StringUtils.isEmpty(token)) {
                return unauthorizedResponse(exchange, serverHttpResponse,TOKEN_MISSION);
            }
            Boolean expired = jwtTokenUtil.isTokenExpired(token);
            //令牌是否过期或无效
            if (expired){
                return unauthorizedResponse(exchange, serverHttpResponse,TOKEN_INVALID);
            }

            //可添加参数
            return chain.filter(exchange.mutate().request(mutate.build()).build());
        };
    }

    /**
     * 获取令牌
     *
     * @param request 请求
     * @return {@link String}
     */
    private String getToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(authJwtProperties.getHeader());
        return token;
    }


    /**
     * 错误处理
     *
     * @param exchange           交易所
     * @param serverHttpResponse 服务器http响应
     * @param errorCode          错误代码
     * @return {@link Mono}<{@link Void}>
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, ServerHttpResponse serverHttpResponse, ErrorCode errorCode) {
        log.warn("token异常处理,请求路径:{}", exchange.getRequest().getPath());
        serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
        serverHttpResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        BaseResult responseResult = new BaseResult<>(errorCode);
        DataBuffer dataBuffer = serverHttpResponse.bufferFactory()
                .wrap(JSON.toJSONStringWithDateFormat(responseResult, JSON.DEFFAULT_DATE_FORMAT)
                        .getBytes(StandardCharsets.UTF_8));
        return serverHttpResponse.writeWith(Flux.just(dataBuffer));
    }

}
