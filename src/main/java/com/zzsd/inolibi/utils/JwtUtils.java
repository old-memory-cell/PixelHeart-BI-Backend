package com.zzsd.inolibi.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtils {

    private static final String SIGN_KEY_STR = "aW5vbGktYmktc2VjcmV0LWtleS1zdHI=";
    private static Long expire = 43200000L;

    // 转换为 SecretKey
    private static final SecretKey signKey =
            Keys.hmacShaKeyFor(SIGN_KEY_STR.getBytes(StandardCharsets.UTF_8));

    /**
     * 生成JWT令牌
     * @return jwt
     */
    public static String generateJwt(Map<String,Object> claims){
        String jwt = Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, signKey)
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .compact();
        return jwt;
    }

    /**
     * 解析JWT令牌
     * @param jwt JWT令牌
     * @return JWT第二部分负载 payload 中存储的内容
     */
    public static Claims parseJWT(String jwt) throws  Exception{
        Claims claims = Jwts.parser()
                .verifyWith(signKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
        return claims;
    }
}
