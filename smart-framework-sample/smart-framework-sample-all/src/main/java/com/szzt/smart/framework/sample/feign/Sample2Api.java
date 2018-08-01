package com.szzt.smart.framework.sample.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.szzt.smart.framework.web.model.ResultBody;

@FeignClient(name = "SAMPLE2", fallbackFactory = Sample2ApiFallback.class)
public interface Sample2Api
{
    
    @RequestMapping(value = "trace/trace2", method = RequestMethod.POST)
    public ResultBody<Void> trace2();
}
