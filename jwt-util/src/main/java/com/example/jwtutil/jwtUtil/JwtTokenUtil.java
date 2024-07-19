package com.example.jwtutil.jwtUtil;

import com.example.jwtutil.config.AuthJwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * jwt令牌工具类
 *
 * @author fxab
 * @date 2024/07/17
 */
@Component
public class JwtTokenUtil {

    /**
     * 过期时间
     */
    private static final String EXPIRE_IN = "expire_in";


    /**
     * jwt配置
     */
    @Resource
    private AuthJwtProperties jwtProperties;


    /**
     * redis工具类
     */
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 生成令牌和刷新令牌
     *
     * @param userId  用户id
     * @param account
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    public Map<String, Object> generateTokenAndRefreshToken(String userId, String account) {
        //生成令牌及刷新令牌
        Map<String, Object> tokenMap = buildToken(userId, account);
        //redis缓存结果
        cacheToken(userId, tokenMap);
        return tokenMap;
    }

    /**
     * 构建令牌
     *
     * @param userId  用户id
     * @param account 帐户
     * @return {@link Map}<{@link String}, {@link Object}>
     */ //生成令牌
    private Map<String, Object> buildToken(String userId, String account) {
        //生成token令牌
        String accessToken = generateClaims(userId, account, null);
        //生成刷新令牌
        String refreshToken = generateRefreshClaims(userId, account, null);
        //存储两个令牌及过期时间，返回结果
        HashMap<String, Object> tokenMap = new HashMap<>(2);
        tokenMap.put(jwtProperties.getACCESS_TOKEN(), accessToken);
        tokenMap.put(jwtProperties.getREFRESH_TOKEN(), refreshToken);
        tokenMap.put(EXPIRE_IN, jwtProperties.getExpire());
        return tokenMap;
    }

    /**
     * 生成 token令牌声明
     *
     * @param payloads 令牌中携带的附加信息
     * @param userId   用户id
     * @param account  帐户
     * @return 令牌
     */
    public String generateClaims(String userId, String account,
                                        Map<String, String> payloads) {
        Map<String, Object> claims = buildClaims(userId, account, payloads);
        return generateToken(claims);
    }

    /**
     * 生成刷新令牌声明
     *
     * @param userId   用户id
     * @param username 用户名
     * @param payloads 有效载荷
     * @return {@link String}
     */
    public String generateRefreshClaims(String userId, String username, Map<String, String> payloads) {
        Map<String, Object> claims = buildClaims(userId, username, payloads);
        return generateRefreshToken(claims);
    }

    /**
     * 构建map存储令牌需携带的信息
     *
     * @param userId   用户id
     * @param username 用户名
     * @param payloads 有效载荷
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    private Map<String, Object> buildClaims(String userId, String username, Map<String, String> payloads) {
        int payloadSizes = payloads == null? 0 : payloads.size();

        Map<String, Object> claims = new HashMap<>(payloadSizes + 2);
        claims.put("sub", userId);
        claims.put(jwtProperties.getUSER_NAME(), username);
        claims.put("created", new Date());
        if(payloadSizes > 0){
            claims.putAll(payloads);
        }
        return claims;
    }




    /**
     * 生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String generateToken(Map<String, Object> claims) {
        Date expirationDate = new Date(System.currentTimeMillis()
                + jwtProperties.getExpire());
        return Jwts.builder().setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512,
                        jwtProperties.getSecret())
                .compact();
    }

    /**
     * 生成刷新令牌 refreshToken，有效期是令牌的 2 倍
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String generateRefreshToken(Map<String, Object> claims) {
        Date expirationDate = new Date(System.currentTimeMillis() + jwtProperties.getExpire() * 2);
        return Jwts.builder().setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecret())
                .compact();
    }


    /**
     * 缓存令牌
     *
     * @param userId   用户id
     * @param tokenMap 令牌映射
     */
    private void cacheToken(String userId, Map<String, Object> tokenMap) {
        stringRedisTemplate.opsForHash().put(jwtProperties.getJWT_CACHE_KEY() + userId, jwtProperties.getACCESS_TOKEN(), tokenMap.get(jwtProperties.getACCESS_TOKEN()));
        stringRedisTemplate.opsForHash().put(jwtProperties.getJWT_CACHE_KEY() + userId, jwtProperties.getREFRESH_TOKEN(), tokenMap.get(jwtProperties.getREFRESH_TOKEN()));
        stringRedisTemplate.expire(userId, jwtProperties.getExpire() * 2, TimeUnit.MILLISECONDS);
    }




    /**
     * 从令牌中获取数据声明,验证 JWT 签名
     *
     * @param token 令牌
     * @return 数据声明
     */
    public Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(jwtProperties.getSecret()).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    /**
     * 刷新令牌并生成新令牌
     * 并将新结果缓存进redis
     *
     * @param userId   用户id
     * @param username 用户名
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    public Map<String, Object> refreshTokenAndGenerateToken(String userId, String username) {
        Map<String, Object> tokenMap = buildToken(userId, username);
        stringRedisTemplate.delete(jwtProperties.getJWT_CACHE_KEY() + userId);
        cacheToken(userId, tokenMap);
        return tokenMap;
    }



    /**
     * 删除令牌
     *
     * @param userId 用户id
     * @return boolean
     */
    public boolean removeToken(String userId) {
        return Boolean.TRUE.equals(stringRedisTemplate.delete(jwtProperties.getJWT_CACHE_KEY() + userId));
    }

    /**
     * 从令牌中获取用户id
     *
     * @param token 令牌
     * @return 用户id
     */
    public String getUserIdFromToken(String token) {
        String userId;
        try {
            Claims claims = getClaimsFromToken(token);
            userId = claims.getSubject();
        } catch (Exception e) {
            userId = null;
        }
        return userId;
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUserNameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = (String) claims.get(jwtProperties.getUSER_NAME());
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 判断令牌是否不存在 redis 中
     *
     * @param token 刷新令牌
     * @return true=不存在，false=存在
     */
    public Boolean isRefreshTokenNotExistCache(String token) {
        String userId = getUserIdFromToken(token);
        String refreshToken = (String)stringRedisTemplate.opsForHash().get(jwtProperties.getJWT_CACHE_KEY() + userId, jwtProperties.getREFRESH_TOKEN());
        return refreshToken == null || !refreshToken.equals(token);
    }

    /**
     * 判断令牌是否无效或过期
     *
     * @param token 令牌
     * @return true=已过期，false=未过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            //验证 JWT 签名失败或令牌过期
            return true;
        }
    }

    /**
     * 刷新令牌
     *
     * @param token 原令牌
     * @return 新令牌
     */
    public String refreshToken(String token) {
        String refreshedToken;
        try {
            Claims claims = getClaimsFromToken(token);
            claims.put("created", new Date());
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }


    /**
     * 验证令牌
     *
     * @param token  令牌
     * @param userId 用户Id用户名
     * @return 是否有效
     */
    public Boolean validateToken(String token, String userId) {

        String userIdByToken = getUserIdFromToken(token);
        return (userIdByToken.equals(userId) && !isTokenExpired(token));
    }
















}
