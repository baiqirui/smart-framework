package com.szzt.smart.framework.swagger;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * swagger配置属性
 *
 * @author
 * @Date 2017-10-12 16:59
 */
@Data
public class SwaggerHeader
{
    /**
     * 请求头参数的名称
     */
    private String name;

    /**
     * 请求头是否必须(默认为true)
     */
    private boolean required = true;

    /**
     * 请求头参数类型(string)
     */
    private String type = "string";

    /**
     * 请求头参数描述
     */
    private String description;


}
