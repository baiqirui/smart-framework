package com.szzt.smart.framework.metrics;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "metrics")
@Validated
public class MetricsProperties
{
    
    private static final int DEFAULT_INTERVAL = 5;
    
    private static final int DEFAULT_MAX_SIZE = 10;

    private Integer interval = DEFAULT_INTERVAL; // 度量间隔单位秒

    private Integer maxSize = DEFAULT_MAX_SIZE;
    
    private InfluxdbConfig influxdb;
    
    @NotBlank(message = "appName 不能为空!")
    private String appName;

    private List<String> urlPatterns; //需要监控的接口地址，如：/api/*


}
