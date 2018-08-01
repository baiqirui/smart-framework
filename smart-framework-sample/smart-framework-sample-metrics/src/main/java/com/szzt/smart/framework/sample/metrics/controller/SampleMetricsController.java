package com.szzt.smart.framework.sample.metrics.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.szzt.smart.framework.web.model.ResultBody;

@RestController
public class SampleMetricsController
{
    
    @GetMapping("/api/ok")
    public ResultBody<String> apiOk()
    {
        return ResultBody.success("apiOk");
    }

    @GetMapping("/kpi/ok")
    public ResultBody<String> kpiOk()
    {
        return ResultBody.success("kpiOk");
    }

    @GetMapping("/ok")
    public ResultBody<String> ok()
    {
        return ResultBody.success("ok");
    }
    
    @GetMapping("/api/test/{value}")
    public ResultBody<String> path(@PathVariable("value") String value)
    {
        return ResultBody.success(value);
    }
    
}
