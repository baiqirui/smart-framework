package com.szzt.smart.framework.swagger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * swagger配置属性
 *
 * @author
 * @Date 2017-10-12 16:59
 */
@Data
@ConfigurationProperties(prefix = "smart.swagger")
public class SwaggerProperties
{
    private String groupName;
    
    private String basePackage;
    
    private String title;
    
    private String version;
    
    private String description;

    private List<SwaggerHeader> headers;
    
}
