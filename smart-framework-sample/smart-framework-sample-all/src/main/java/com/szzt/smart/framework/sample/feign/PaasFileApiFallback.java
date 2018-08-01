package com.szzt.smart.framework.sample.feign;

import feign.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.szzt.smart.framework.feign.FeignConfiguration;

import feign.hystrix.FallbackFactory;

@Component
public class PaasFileApiFallback implements FallbackFactory<PaasFileApi>
{
    
    @Autowired
    private FeignConfiguration.FeignExceptionHandler feignExceptionHandler;
    
    @Override
    public PaasFileApi create(Throwable throwable)
    {
        return new PaasFileApi()
        {
            @Override
            public Response getFile(String fileId) {
                return null;
            }
        };
    }
}
