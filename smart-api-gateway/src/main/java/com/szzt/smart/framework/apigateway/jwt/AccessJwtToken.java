package com.szzt.smart.framework.apigateway.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.jsonwebtoken.Claims;
import lombok.Data;

@Data
public final class AccessJwtToken
{
    private  String token;
    
    @JsonIgnore
    private Claims claims;

    public AccessJwtToken(String token, Claims claims)
    {
        this.token = token;
        this.claims = claims;
    }

    public AccessJwtToken(String token)
    {
        this.token = token;
    }

    public AccessJwtToken()
    {

    }
    
}
