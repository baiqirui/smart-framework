package com.szzt.smart.framework.apigateway.feign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.szzt.smart.framework.apigateway.jwt.AccessJwtToken;
import com.szzt.smart.framework.feign.FeignConfiguration;
import com.szzt.smart.framework.web.model.ResultBody;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MobileAppFeignFailBackFactory implements FallbackFactory<MobileAppFeign>
{
    
    @Autowired
    private FeignConfiguration.FeignExceptionHandler feignExceptionHandler;
    
    @Override
    public MobileAppFeign create(final Throwable throwable)
    {
        return new MobileAppFeign()
        {
            @Override
            public ResultBody<AccessJwtToken> parseToken(AccessJwtToken token)
            {
                return feignExceptionHandler.handleException(throwable);
            }
        };

    }
}
