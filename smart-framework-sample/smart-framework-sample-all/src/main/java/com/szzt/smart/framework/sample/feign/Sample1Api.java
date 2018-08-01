package com.szzt.smart.framework.sample.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.szzt.smart.framework.web.model.ResultBody;

@FeignClient(name = "SAMPLE1", fallbackFactory = Sample1ApiFallback.class)
public interface Sample1Api
{
    
    @RequestMapping(value = "trace/trace1", method = RequestMethod.POST)
    public ResultBody<Void> trace1();
}
