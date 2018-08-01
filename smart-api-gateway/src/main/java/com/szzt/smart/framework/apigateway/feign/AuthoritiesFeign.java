package com.szzt.smart.framework.apigateway.feign;

import com.szzt.smart.framework.apigateway.jwt.AccessJwtToken;
import com.szzt.smart.framework.web.model.ResultBody;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "USER-CENTER-SERVICE",  fallbackFactory = AuthoritiesFeignFailBackFactory.class )
public interface AuthoritiesFeign
{
    @RequestMapping(method = RequestMethod.POST, value = "/parseToken")
    public ResultBody<AccessJwtToken> parseToken(@RequestBody AccessJwtToken token);

}
