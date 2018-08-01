package com.szzt.smart.framework.apigateway.jwt;

import com.szzt.smart.framework.apigateway.config.JwtFilterConfig;
import lombok.Data;

@Data
public class UserContext
{
    private String userName;
    
    private String userId;

    private JwtProperties properties;

    private JwtFilterConfig config;

}
