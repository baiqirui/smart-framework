package com.szzt.smart.framework.apigateway.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.szzt.smart.framework.apigateway.jwt.AccessJwtToken;
import com.szzt.smart.framework.web.model.ResultBody;

@FeignClient(value = "MOBILE-BUSINESS", fallbackFactory = MobileAppFeignFailBackFactory.class)
public interface MobileAppFeign
{
    @RequestMapping(method = RequestMethod.POST, value = "/parseToken")
    public ResultBody<AccessJwtToken> parseToken(@RequestBody AccessJwtToken accessJwtToken);
    
}
