package com.szzt.smart.framework.sample.feign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.szzt.smart.framework.feign.FeignConfiguration;
import com.szzt.smart.framework.web.model.ResultBody;

import feign.hystrix.FallbackFactory;

@Component
public class Sample1ApiFallback implements FallbackFactory<Sample1Api>
{
    
    @Autowired
    private FeignConfiguration.FeignExceptionHandler feignExceptionHandler;
    
    @Override
    public Sample1Api create(Throwable throwable)
    {
        return new Sample1Api()
        {
            @Override
            public ResultBody<Void> trace1() {
                return null;
            }
        };
    }
}
