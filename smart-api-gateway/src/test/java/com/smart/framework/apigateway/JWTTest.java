package com.smart.framework.apigateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;

import java.util.Date;

public class JWTTest
{
    private final static String key = "123456";
    
    @Test
    public void testJwt()
    {
        // Key key = MacProvider.generateKey();
        // encodeStr = base64(Header).base64(Payload)
        // jwt = base64(Header).base64(Payload).sign(encodeStr, key)
        // header = { "alg": "HS256", "typ": "JWT"}
        /**
         * payload = reserved-claim + private-claim + public-claim reserved-claim = 常用的有 iss（签发者）,exp（过期时间戳）,
         * sub（面向的用户）, aud（接收方）, iat（签发时间） private-claim = 私有的一些声明信息 根据需要定义自己的字段 public-claim = 公开的一些声明 据需要定义自己的字段
         */
        // Claims claims = Claims.AUDIENCE
        String jsonWebToken = Jwts.builder()
            .setHeaderParam("typ", "JWT")
            // .setHeader()
            // .setPayload()
            .claim("username", "Bob")
            // .setClaims()
            .setAudience("Android App") // 接收方
            .setExpiration(new Date(System.currentTimeMillis() + 6000 * 10)) // 过期时间戳
            .setIssuer("cloud") // 签发者
            .setIssuedAt(new Date()) // 签发时间
            .setSubject("Smart") // 面向的用户
            .signWith(SignatureAlgorithm.HS512, key)
            .compact();
        System.out.println(jsonWebToken);
        System.out.println(
            Jwts.parser().setSigningKey(key).parseClaimsJws(jsonWebToken).getBody().getSubject().equals("Smart"));
    }
    
    @Test
    public void parseJWT()
    {
        String jsonWebToken = "";
        try
        {
            Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jsonWebToken).getBody();
            System.out.println(claims);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
}
