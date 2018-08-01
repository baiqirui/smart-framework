package com.szzt.smart.framework.apigateway.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "smart.jwt")
public class JwtProperties
{

    /**
     * token过期时间
     */
    private Integer expirationTime;

    /**
     * Token issuer.
     */
    private String tokenIssuer;
    
    /**
     * 密钥
     */
    private String key;


    
}
