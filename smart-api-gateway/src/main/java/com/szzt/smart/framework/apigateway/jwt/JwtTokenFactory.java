package com.szzt.smart.framework.apigateway.jwt;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenFactory
{
    private final JwtProperties jwtProperties;
    
    @Autowired
    public JwtTokenFactory(JwtProperties jwtProperties)
    {
        this.jwtProperties = jwtProperties;
    }
    
    /**
     * 创建token
     * 
     * @param userContext
     * @return
     */
    public AccessJwtToken createAccessJwtToken(UserContext userContext)
    {
        if (StringUtils.isBlank(userContext.getUserId()) || StringUtils.isBlank(userContext.getUserName()))
            throw new IllegalArgumentException("Cannot create JWT Token without username and userId");
        
        Claims claims = Jwts.claims().setSubject(userContext.getUserName());
        claims.put("userName", userContext.getUserName());
        claims.put("userId", userContext.getUserId());
        
        LocalDateTime currentTime = LocalDateTime.now();
        
        String token = Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setClaims(claims)
            .setIssuer(jwtProperties.getTokenIssuer()) // token签发者
            .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant())) // 签发时间
            .setExpiration(Date.from(
                currentTime.plusMinutes(jwtProperties.getExpirationTime()).atZone(ZoneId.systemDefault()).toInstant())) // token的有效时间
            .signWith(SignatureAlgorithm.HS512, jwtProperties.getKey())
            .compact();
        
        return new AccessJwtToken(token, claims);
    }
    
    /**
     * jwt解析
     * @param jsonWebToken
     * @return
     */
    public Claims parseJWT(String jsonWebToken)
    {
        try
        {
            Claims claims = Jwts.parser().setSigningKey(jwtProperties.getKey()).parseClaimsJws(jsonWebToken).getBody();
            return claims;
        }
        catch (Exception ex)
        {
            return null;
        }
    }
}
