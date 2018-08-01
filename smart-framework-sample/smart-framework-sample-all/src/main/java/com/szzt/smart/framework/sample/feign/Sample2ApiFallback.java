package com.szzt.smart.framework.sample.feign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.szzt.smart.framework.feign.FeignConfiguration;
import com.szzt.smart.framework.web.model.ResultBody;

import feign.hystrix.FallbackFactory;

@Component
public class Sample2ApiFallback implements FallbackFactory<Sample2Api>
{
    
    @Autowired
    private FeignConfiguration.FeignExceptionHandler feignExceptionHandler;
    
    @Override
    public Sample2Api create(Throwable throwable)
    {
        return new Sample2Api()
        {
            @Override
            public ResultBody<Void> trace2()
            {
                return null;
            }
        };
    }
}
