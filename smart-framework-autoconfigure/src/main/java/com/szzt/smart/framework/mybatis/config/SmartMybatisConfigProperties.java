package com.szzt.smart.framework.mybatis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "smartMybatis")
public class SmartMybatisConfigProperties
{
    private String configLocation;
    
    private String mapperLocation;
    
    private String aliasPackage;
    
    private String mapperScanner;
}
