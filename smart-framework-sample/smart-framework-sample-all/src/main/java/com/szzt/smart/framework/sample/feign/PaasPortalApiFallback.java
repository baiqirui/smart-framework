package com.szzt.smart.framework.sample.feign;

import java.util.List;

import com.szzt.smart.framework.feign.FeignConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;

@Component
public class PaasPortalApiFallback implements FallbackFactory<PaasPortalApi>
{
    
    @Autowired
    private FeignConfiguration.FeignExceptionHandler feignExceptionHandler;
    
    @Override
    public PaasPortalApi create(Throwable throwable)
    {
        return new PaasPortalApi()
        {
            @Override
            public List<ParamConfig> listParamConfig(ParamConfigCondition paramConfigCondition)
            {
                System.out.println(feignExceptionHandler.handleException(throwable));
                return null;
            }
        };
    }
}
