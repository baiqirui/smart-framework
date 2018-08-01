package com.szzt.smart.framework.shutdown;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "shutdown.graceful")
@Data
public class GracefulShutdownProperties
{
    
    // 单位秒
    private Integer wait = 30;
    
    // 单位秒
    private Integer timeout = 30;
    
}
