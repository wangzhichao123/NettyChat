package com.wzc.netty.service.Impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.wzc.netty.exception.BizException;
import com.wzc.netty.pojo.dto.UserDetailsDTO;
import com.wzc.netty.service.RedisService;
import com.wzc.netty.service.TokenService;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.wzc.netty.constant.RedisConstant.*;
import static com.wzc.netty.constant.TokenConstant.*;
import static com.wzc.netty.enums.StatusCodeEnum.INVALID_TOKEN;

@Service
@Slf4j
public class TokenServiceImpl implements TokenService {

    @Resource
    private RedisService redisService;

    /**
     * 生成用户token,设置token超时时间
     * ID (id): JWT 的唯一标识符,用于标识每个 JWT
     * 主体 (subject): 用户的唯一标识符
     */
    @Override
    public String createToken(UserDetailsDTO userDetailsDTO) {
        return createToken(userDetailsDTO.getUserId());

    }

    @Override
    public String createToken(String subject) {
        return Jwts.builder()
                .id(UUID.randomUUID().toString().replaceAll("-", ""))
                .subject(subject)
                .issuer("surprise")
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION * 1000))
                .signWith(generalKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 校验token并解析token
     */
    @Override
    public String getSubject(String token) {
        String subject = "";
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(generalKey())
                    .build()
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""));
            subject = claimsJws.getBody().getSubject();
        }catch (JwtException ex) {
            log.info("JWT 解析失败");
            throw new BizException(INVALID_TOKEN);
        }
        return subject;
    }

    /**
     * 如果用户登录成功、那么 Redis 就存有用户信息
     * @param token
     * @return
     */
    @Override
    public boolean verifyToken(String token) {
        String userId = getSubject(token);
        if (StrUtil.isBlank(userId)) {
            return false;
        }
        // 为了 Redis 存储的数据类型是标准版 JSON 数据类型
        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) redisService.hGet(LOGIN_USER, userId);
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(map));
        UserDetailsDTO userDetailsDTO = jsonObject.toJavaObject(UserDetailsDTO.class);
        return ObjectUtil.isNotNull(userDetailsDTO);
    }

    /**
     * 预保留方法
     * @param request
     * @return
     */
    @Override
    public UserDetailsDTO getUserDetailDTO(HttpServletRequest request) {
        String token = Optional.ofNullable(request.getHeader(TOKEN_HEADER)).orElse("").replaceFirst(TOKEN_PREFIX, "");
        if (StringUtils.hasText(token) && !token.equals("null")) {
            String userId = getSubject(token);
            LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) redisService.hGet(LOGIN_USER, userId);
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(map));
            return jsonObject.toJavaObject(UserDetailsDTO.class);
        }
        return null;
    }

    /**
     * 生成密钥
     * 如果长度不足，可以采取以下措施之一：
     * a. 重复填充直到达到256字节
     * b. 使用哈希函数处理以增加长度
     * 这里我们选择方案 a 进行填充
     */
    private static SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(SECRET);
        // 检查密钥长度是否满足要求
        if (encodedKey.length < 256) {
            byte[] expandedKey = new byte[256];
            System.arraycopy(encodedKey, 0, expandedKey, 0, encodedKey.length);
            // 填充剩余部分，这里简单地使用0填充，实际中可以使用更安全的方法
            for (int i = encodedKey.length; i < expandedKey.length; i++) {
                expandedKey[i] = 0;
            }
            encodedKey = expandedKey;
        }
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "HmacSHA256");
    }
}
